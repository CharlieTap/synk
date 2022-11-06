package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.diff.ObjectDiffer
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.meta.transformer.MetaTransformer
import com.tap.synk.relay.MessageMonoid
import com.tap.synk.resolver.IDResolver

interface SynkContract{

    var hlc : HybridLogicalClock
    val factory : MetaStoreFactory
    val cache : ReflectionsCache
    val merger : MessageMonoid<Any>
    val idResolver : IDResolver<Any>
    val metaTransformer : MetaTransformer<Any>
    val differ : ObjectDiffer<Any>

}
