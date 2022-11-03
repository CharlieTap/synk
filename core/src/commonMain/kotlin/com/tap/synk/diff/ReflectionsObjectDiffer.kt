package com.tap.synk.diff

import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

class ReflectionsObjectDiffer(

): ObjectDiffer<Any> {
    override fun diff(old: Any, new: Any): Set<String> {

        return new::class.declaredMemberProperties.filter { prop ->

            prop.isAccessible = true

            val oldValue = prop.getter.call(old)
            val newValue = prop.getter.call(new)

            oldValue != newValue
        }.fold(mutableSetOf()) { acc, prop ->
          acc.apply {
              add(prop.name)
          }
        }

    }
}