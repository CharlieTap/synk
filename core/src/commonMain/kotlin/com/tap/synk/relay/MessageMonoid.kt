package com.tap.synk.relay

import com.tap.synk.abstraction.Monoid
import com.tap.synk.cache.ParamPropPair
import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.meta.Meta
import com.tap.synk.meta.MetaMonoid
import kotlin.reflect.jvm.isAccessible

class MessageMonoid<T : Any>(
    private val reflectionsCache: ReflectionsCache,
    private val metaMonoid: Monoid<Meta>
) : Monoid<Message<T>> {

    override val neutral: Message<T>
        get() = Message(Unit as T, MetaMonoid.neutral)

    override fun combine(a: Message<T>, b: Message<T>): Message<T> {
        val constructor = reflectionsCache.getConstructor(a.crdt::class)
        val paramsProps = reflectionsCache.getParamsAndProps(a.crdt::class)
        val parameters = paramsProps.map(ParamPropPair::first)
        val props = paramsProps.map(ParamPropPair::second)

        val meta = metaMonoid.combine(a.meta, b.meta)
        val crdt = parameters.map { param ->

            val ahlc = a.meta.timestampMeta[param.name]
            val winner = meta.timestampMeta[param.name]

            val prop = props.first { it.name == param.name }
            prop.isAccessible = true

            if (ahlc == winner) {
                prop.getter.call(a.crdt)
            } else {
                prop.getter.call(b.crdt)
            }
        }.let { params ->
            constructor.call(*params.toTypedArray())
        }

        return Message(crdt, meta)
    }
}
