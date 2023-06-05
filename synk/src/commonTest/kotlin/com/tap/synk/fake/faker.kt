package com.tap.synk.fake

import com.benasher44.uuid.Uuid
import com.tap.synk.CRDT
import io.github.serpro69.kfaker.Faker

val faker = Faker()

internal fun crdt(id: String = Uuid.randomUUID().toString()): CRDT = CRDT(
    id,
    faker.name.firstName(),
    faker.name.lastName(),
    faker.random.nextInt(),
)
