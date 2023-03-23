package com.tap.synk.serialize

class EnumStringSerializer<T : Enum<T>>(
    private val values: Array<out T>
) : StringSerializer<T> {
    override fun serialize(serializable: T): String {
        return serializable.ordinal.toString()
    }

    override fun deserialize(serialized: String): T {
        return values.first { it.ordinal.toString() == serialized }
    }
}

inline fun <reified T : Enum<T>> EnumStringSerializer(): EnumStringSerializer<T> {
    return EnumStringSerializer(enumValues())
}
