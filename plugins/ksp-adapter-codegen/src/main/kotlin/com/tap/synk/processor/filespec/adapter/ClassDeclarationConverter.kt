package com.tap.synk.processor.filespec.adapter

import com.tap.synk.processor.context.AdapterContext

context(AdapterContext)
internal fun synkAdapter(): SynkAdapter {
    return SynkAdapter(
        synkAdapterClassName,
        deriveInterfaces(),
        deriveParameters(),
    )
}

context(AdapterContext)
private fun deriveInterfaces(): Set<AdapterInterface> {
   return setOf(
       Inherited(customSynkAdapterTypeName),
       Delegated(customIdResolverTypeName, AdapterContext.VARIABLE_NAME_ID_RESOLVER),
       Delegated(customMapEncoderTypeName, AdapterContext.VARIABLE_NAME_MAP_ENCODER),
   )
}

context(AdapterContext)
private fun deriveParameters(): List<AdapterParameter> {
   return listOf(
       AdapterParameter(
           AdapterContext.VARIABLE_NAME_ID_RESOLVER,
           customIdResolverTypeName,
           idResolverClassName
       ),
       AdapterParameter(
           AdapterContext.VARIABLE_NAME_MAP_ENCODER,
           customMapEncoderTypeName,
           mapEncoderClassName
       ),
   )
}