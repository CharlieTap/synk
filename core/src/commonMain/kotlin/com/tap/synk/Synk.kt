package com.tap.synk

import com.tap.hlc.HybridLogicalClock
import com.tap.synk.cache.ReflectionsCache
import com.tap.synk.diff.ObjectDiffer
import com.tap.synk.diff.ReflectionsObjectDiffer
import com.tap.synk.meta.MetaMonoid
import com.tap.synk.meta.store.InMemoryMetaStoreFactory
import com.tap.synk.meta.store.MetaStoreFactory
import com.tap.synk.meta.transformer.MetaTransformer
import com.tap.synk.meta.transformer.ReflectionsMetaTransformer
import com.tap.synk.relay.MessageMonoid
import com.tap.synk.resolver.IDResolver
import com.tap.synk.resolver.ReflectionsIDResolver

class Synk(
    override var hlc : HybridLogicalClock = HybridLogicalClock(),// todo load from storage or newest object
    override val factory : MetaStoreFactory = InMemoryMetaStoreFactory(),
    override val cache : ReflectionsCache = ReflectionsCache(),
    override val merger : MessageMonoid<Any> = MessageMonoid<Any>(cache, MetaMonoid),
    override val idResolver : IDResolver<Any> = ReflectionsIDResolver(cache),
    override val metaTransformer : MetaTransformer<Any> = ReflectionsMetaTransformer(cache),
    override val differ : ObjectDiffer<Any> = ReflectionsObjectDiffer(cache)
) : SynkContract