package com.tap.synk.resolver

import com.tap.synk.cache.ReflectionsCache

class ReflectionsIDResolver(
    private val reflectionsCache: ReflectionsCache = ReflectionsCache()
//    private val stringType: KType = String::class.createType()
) : IDResolver<Any> {

//    override fun populateID(crdt: Any): Any {
//
//        val constructor = reflectionsCache.getConstructor(crdt::class)
//        val paramsProps = reflectionsCache.getParamsAndProps(crdt::class)
//        val parameters = paramsProps.map(ParamPropPair::first)
//        val props = paramsProps.map(ParamPropPair::second)
//
//        val idProp =  props.firstOrNull {
//            it.name.lowercase() == "id"
//        } ?: throw IllegalStateException("Unable to find ID property for CRDT")
//
//        val populated = parameters.map { param ->
//
//            val prop = props.first { it.name == param.name }
//            prop.isAccessible = true
//
//            if(prop.name == idProp.name) {
//                require(prop.returnType == stringType) { "IDs must be UUIDs of string type"}
//                Uuid.randomUUID().toString()
//            } else {
//                prop.getter.call(crdt)
//            }
//
//        }.let { params ->
//            constructor.call(*params.toTypedArray())
//        }
//
//        return populated
//    }

    override fun invoke(crdt: Any): String? {
        val idProp = reflectionsCache.getProps(crdt::class).firstOrNull {
            it.name.lowercase() == "id"
        } ?: return null

        return idProp.getter.call(crdt)?.toString()
    }
}
