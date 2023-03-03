package com.tap.synk.encode

import com.tap.synk.IDCRDT

internal class IDCRDTMapEncoder : MapEncoder<IDCRDT> {
    override fun encode(crdt: IDCRDT): Map<String, String> {
        return mutableMapOf<String, String>().apply {
            put("id", crdt.id)
            put("name", crdt.name)
            put("last_name", crdt.last_name)
            put("phone", crdt.phone.toString())
        }
    }

    override fun decode(map: Map<String, String>): IDCRDT {
        return IDCRDT(
            map["id"]!!,
            map["name"]!!,
            map["last_name"]!!,
            map["phone"]!!.toInt(),
        )
    }
}