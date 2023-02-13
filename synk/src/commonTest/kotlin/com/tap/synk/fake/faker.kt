package com.tap.synk.fake

import com.benasher44.uuid.Uuid
import com.tap.synk.IDCRDT
import io.github.serpro69.kfaker.Faker

val faker = Faker()

internal fun crdt(id: String = Uuid.randomUUID().toString()): IDCRDT = IDCRDT(
    id,
    faker.name.firstName(),
    faker.name.lastName(),
    faker.random.nextInt()
)
