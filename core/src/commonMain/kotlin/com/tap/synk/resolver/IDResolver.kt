package com.tap.synk.resolver

fun interface IDResolver<T> : (T) -> String?
