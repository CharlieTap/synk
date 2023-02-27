package com.tap.synk.adapter

import com.tap.synk.encode.MapEncoder
import com.tap.synk.resolver.IDResolver

interface SynkAdapter<T : Any> : IDResolver<T>, MapEncoder<T>
