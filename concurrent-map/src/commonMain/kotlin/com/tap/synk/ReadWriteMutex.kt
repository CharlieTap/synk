package com.tap.synk


import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.SelectClause2
import kotlinx.coroutines.sync.Mutex

import javax.naming.OperationNotSupportedException
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic

interface ReadWriteMutex {
    val read: Mutex
    val write: Mutex
    val state: LockState

    enum class LockState {

        // at least one read lock is acquired (returned from lock fun)
        READ_LOCKED,

        // write lock is acquired, in this state new readers/writers will -obviously-
        // suspended -inside lock()- until the current writer release his lock,
        // in the same time keep in mind that the writer itself may have been
        // already returned from lock fun OR it may be still suspended -inside lock()-
        // waiting for old readers to release their locks
        WRITE_LOCKED,

        // no lock is acquired
        UNLOCKED,
    }

    fun ensure(targetState: LockState)
    fun ensureWriteLocked()
    fun ensureReadLocked()
    fun ensureUnlocked()
}

fun ReadWriteMutex(): ReadWriteMutex {
    val mutex = Mutex()
    val writePermissions = Channel<Unit>()
    val readPermissions = Channel<Unit>()
    val pendingCount = atomic(0)
    val readersDeparting = atomic(0)
    return SimpleReadWriteMutex(
        pendingCount,
        ReaderMutex(mutex, writePermissions, readPermissions, pendingCount, readersDeparting),
        WriterMutex(mutex, writePermissions, readPermissions, pendingCount, readersDeparting),
    )
}

// simple and efficient non-reentrant read write mutex without timeout shenanigans
// ReaderMutex does not track any locks owners, WriterMutex track only the current writer owner
internal class SimpleReadWriteMutex(
    private val pendingCount: AtomicInt,
    override val read: ReaderMutex,
    override val write: WriterMutex,
) : ReadWriteMutex {
    override val state: ReadWriteMutex.LockState
        get() {
            val state = pendingCount.value
            return if (state > 0)
                ReadWriteMutex.LockState.READ_LOCKED
            else if (state == 0)
                ReadWriteMutex.LockState.UNLOCKED
            else
                ReadWriteMutex.LockState.WRITE_LOCKED
        }

    override fun ensure(targetState: ReadWriteMutex.LockState) {
        val currentState = state
        if (currentState != targetState)
            throw IllegalStateException("the ReadWriteMutex was expected to be $targetState but was $currentState")
    }

    override fun ensureWriteLocked() {
        ensure(ReadWriteMutex.LockState.WRITE_LOCKED)
    }

    override fun ensureReadLocked() {
        ensure(ReadWriteMutex.LockState.READ_LOCKED)
    }

    override fun ensureUnlocked() {
        ensure(ReadWriteMutex.LockState.UNLOCKED)
    }

    override fun toString(): String = "SimpleReadWriteMutex($state)"
}

internal sealed class AbstractReadOrWriteMutex(
    protected val mutex: Mutex,
    protected val writePermissions: Channel<Unit>,
    protected val readPermissions: Channel<Unit>,
    protected val pendingCount: AtomicInt,
    protected val readersDeparting: AtomicInt,
) : Mutex {

    companion object {
        const val MAX_READERS = 1 shl 30
    }

    @Deprecated(
        message = "Mutex.onLock deprecated without replacement. For additional details please refer to #2794",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith(""),
    )
    override val onLock: SelectClause2<Any?, Mutex>
        get() = throw OperationNotSupportedException("")

    override fun toString(): String {
        val lockState = if (isLocked) "locked" else "unlocked"
        val className = this::class.simpleName ?: "Mutex"
        return "$className($lockState)"
    }

    protected fun overUnlockedException() =
        IllegalStateException("cannot unlock because the mutex is already unlocked!")

    fun debug() = toString() + "{readersDeparting=$readersDeparting,pendingCount=$pendingCount}"
}

internal class ReaderMutex(
    mutex: Mutex,
    writePermissions: Channel<Unit>,
    readPermissions: Channel<Unit>,
    pendingCount: AtomicInt,
    readersDeparting: AtomicInt,
) : AbstractReadOrWriteMutex(mutex, writePermissions, readPermissions, pendingCount, readersDeparting) {

    override val isLocked: Boolean
        get() = pendingCount.value > 0

    override suspend fun lock(owner: Any?) {
        if (owner != null) throw UnsupportedOperationException()
        if (pendingCount.incrementAndGet() < 0) {
            readPermissions.receive()
        }
    }

    override fun unlock(owner: Any?) {
        if (owner != null) throw UnsupportedOperationException()
        val readersCount = pendingCount.decrementAndGet()
        if (readersCount < 0) {
            if (readersCount == -1 || readersCount == -MAX_READERS - 1) {
                throw overUnlockedException()
            }
            if (readersDeparting.decrementAndGet() == 0) {
                writePermissions.trySend(Unit)
            }
        }
    }

    override fun tryLock(owner: Any?): Boolean {
        if (owner != null) throw UnsupportedOperationException()
        while (true) {
            val readersCount = pendingCount.value
            if (readersCount < 0) {
                return false
            }
            if (pendingCount.compareAndSet(readersCount, readersCount + 1)) {
                return true
            }
        }
    }

    override fun holdsLock(owner: Any): Boolean {
        throw UnsupportedOperationException()
    }
}

internal class WriterMutex(
    mutex: Mutex,
    writePermissions: Channel<Unit>,
    readPermissions: Channel<Unit>,
    pendingCount: AtomicInt,
    readersDeparting: AtomicInt,
) : AbstractReadOrWriteMutex(mutex, writePermissions, readPermissions, pendingCount, readersDeparting) {

    override val isLocked: Boolean
        get() = pendingCount.value < 0

    override suspend fun lock(owner: Any?) {
        mutex.lock(owner)
        val oldReadersCount = pendingCount.getAndAdd(-MAX_READERS)
        if (oldReadersCount != 0 && readersDeparting.addAndGet(oldReadersCount) != 0) {
            writePermissions.receive()
        }
    }

    override fun unlock(owner: Any?) {
        val newReadersCount = pendingCount.addAndGet(MAX_READERS)
        if (newReadersCount >= MAX_READERS) {
            throw overUnlockedException()
        }
        repeat(newReadersCount) {
            readPermissions.trySend(Unit)
        }
        mutex.unlock(owner)
    }

    override fun tryLock(owner: Any?): Boolean {
        if (!mutex.tryLock(owner)) {
            return false
        }
        if (!pendingCount.compareAndSet(0, -MAX_READERS)) {
            mutex.unlock(owner)
            return false
        }
        return true
    }

    override fun holdsLock(owner: Any): Boolean = mutex.holdsLock(owner)
}

// not used, but may need it again to test some old school concurrent algorithms later
internal class Condition(val mutex: Mutex = Mutex()) {
    private val signals = Channel<Unit>()

    fun signal(): Boolean = signals.trySend(Unit).isSuccess

    suspend fun waitSignalUnlocked(owner: Any? = null) {
        mutex.unlock(owner)
        signals.receive()
        mutex.lock(owner)
    }

    suspend fun waitSignal() {
        signals.receive()
    }
}