
# Synk Adapters

---

Synk adapters tell synk how to encode and decode user defined types into generic maps it can perform conflict resolution on. For
every type T you intend to use with Synk you must provide a SynkAdapter<T> when constructing your synk instance.

```kotlin
val synk = Synk.Builder(...)
    .registerSynkAdapter(adapter)
    .registerSynkAdapter(adapter2)
    .build()
```

A synk adapter is the intersection of two interfaces, an IDResolver and a MapEncoder.

```kotlin
interface SynkAdapter<T : Any> : IDResolver<T>, MapEncoder<T>
```

### IDResolvers

IDResolvers tell synk about what properties make a particular class instance unique, for the vast majority of cases this will simply be an id property. I.e.

```kotlin
class FooResolver : IDResolver<Foo> {
    override fun resolveId(crdt: Foo): String {
        return crdt.id
    }
}
```
In the case of uniqueness being derived from more than one property, simply return a string which aggregates them.
Synk will take care of hashing them internally.

```kotlin
class FooResolver : IDResolver<Foo> {
    override fun resolveId(crdt: Foo): String {
        return crdt.bar + crdt.baz
    }
}
```

### MapEncoders

Map encoders convert user defined types to generic maps which are then used for conflict resolution. 
For example


```kotlin
class FooEncoder : MapEncoder<Foo> {
    override fun encode(crdt: Foo) : Map<String, String> {
        return mapOf(
            "bar" to crdt.bar,
            "baz" to crdt.baz
        )
    }
    override fun decode(map: Map<String, String>) : Foo {
        return Foo(
            map["bar"],
            map["baz"]
        )
    }
}
```

**Nested Collections and Type expansion**


Synk can only perform conflict resolution on entries in the map returned by the MapEncoder, if for example the encoder 
concatenated a list into a string for a single entry in the map, Synk would lose the ability to finely track the elements
of said list, and thus conflict resolution would be suboptimal.

For example, for a given type Foo

```kotlin
data class Foo(
    val id: String,
    val bar: List<String>
)
```

A suboptimal MapEncoder would be the following

```kotlin
class FooEncoder : MapEncoder<Foo> {
    override fun encode(crdt: Foo) : Map<String, String> {
        return mapOf(
            "id" to crdt.id,
            "bar" to crdt.bar.joinToString()
        )
    }
    override fun decode(map: Map<String, String>) : Foo {
        return Foo(
            map["id"],
            map["bar"].split(",")
        )
    }
}
```

The above encoder would not be able to resolve conflicts on list elements, instead it would wholesale replace the list with 
a list which happens to be updated more recently.

A correct implementation would look like the following

```kotlin
class FooEncoder : MapEncoder<Foo> {
    override fun encode(crdt: Foo) : Map<String, String> {
        return mutableMapOf<String, String>().apply {
            put("id", crdt.id)
            crdt.bar.forEachIndexed { idx, entry ->
                put("bar$idx", entry)
            }
        }
    }
    override fun decode(map: Map<String, String>) : Foo {
        return Foo(
            map["id"]!!,
            map.keys.filter { it.contains("bar") }.sorted()
        )
    }
}
```


