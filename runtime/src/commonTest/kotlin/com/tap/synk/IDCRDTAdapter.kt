package com.tap.synk

import com.tap.synk.adapter.SynkAdapter

internal class IDCRDTAdapter : SynkAdapter<IDCRDT> {

    override fun resolveId(crdt: IDCRDT): String {
        return crdt.id
    }

    override fun encode(crdt: IDCRDT): Map<String, String> {
        return HashMap<String, String>().apply {
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
            map["phone"]!!.toInt()
        )
    }
}
