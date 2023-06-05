package com.tap.synk.encode

import com.tap.synk.CRDT

internal class IDCRDTMapEncoder : MapEncoder<CRDT> {
    override fun encode(crdt: CRDT): Map<String, String> {
        return mutableMapOf<String, String>().apply {
            put("id", crdt.id)
            put("name", crdt.name)
            put("last_name", crdt.last_name)
            put("phone", crdt.phone.toString())
        }
    }

    override fun decode(map: Map<String, String>): CRDT {
        return CRDT(
            map["id"]!!,
            map["name"]!!,
            map["last_name"]!!,
            map["phone"]!!.toInt(),
        )
    }
}
