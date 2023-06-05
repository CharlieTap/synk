package com.tap.synk

import com.tap.synk.adapter.SynkAdapter

internal class CRDTAdapter : SynkAdapter<CRDT> {

    override fun resolveId(crdt: CRDT): String {
        return crdt.id
    }

    override fun encode(crdt: CRDT): Map<String, String> {
        return HashMap<String, String>().apply {
            put("id", crdt.id)
            put("name", crdt.name)
            put("last_name", crdt.last_name)
            crdt.phone?.let { phone -> put("phone", phone.toString()) }
        }
    }

    override fun decode(map: Map<String, String>): CRDT {
        return CRDT(
            map["id"]!!,
            map["name"]!!,
            map["last_name"]!!,
            map["phone"]?.toInt(),
        )
    }
}
