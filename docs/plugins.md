# Plugins 

---

- [Synk Adapter Plugin](#synk-adapter-plugin)


## Synk Adapter Plugin

The Synk Adapter Plugin is a Kotlin Symbol Processing plugin which simplifies the creation of SynkAdapters.

---

## What it does

---

The plugin generates MapEncoders and SynkAdapters given an IDResolver. It's recommended to use the plugin rather than trying to 
create the MapEncoders yourself, this is due to the complexities of CRDTs outlined in the [SynkAdapter documentation](synk-adapters.md).

Say you have a data class Foo you want to use with Synk:

```kotlin
data class Foo(
    private val bar: String,
    private val baz: Int?,
    private val bim: Boolean,
)
```

Synk needs to know what value in Foo makes it unique, so you create an IDResolver


```kotlin
class FooResolver : IDResolver<Foo> {
    override fun resolveId(crdt: Foo): String {
        return crdt.bar
    }
}
```

Now typically you would have to write the MapEncoder and SynkAdapter for Foo also, however if you add the SynkAdapter annotation like below and run build

```kotlin
@SynkAdapter
class FooResolver : IDResolver<Foo> {
    override fun resolveId(crdt: Foo): String {
        return crdt.bar
    }
}
```

The plugin will generate a MapEncoder and SynkAdapter for you (inside the build/generated/ksp directory)

```kotlin
public class FooMapEncoder : MapEncoder<Foo> {
    public override fun encode(crdt: Foo): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map["bar"] = crdt.bar
        crdt.baz?.let { value -> map["baz"] = value.toString()  }
        map["bim"] = crdt.bim.toString()
        return map
    }

    public override fun decode(map: Map<String, String>): Foo {
        val crdt = Foo(
            map["bar"]!!,
            map["baz"]?.toInt(),
            map["bim"]!!.toBoolean(),
        )
        return crdt
    }
}

public class FooSynkAdapter(
    private val idResolver: IDResolver<Foo> = FooResolver(),
    private val mapEncoder: MapEncoder<Foo> = FooMapEncoder(),
) : SynkAdapter<Foo>, IDResolver<Foo> by idResolver, MapEncoder<Foo> by mapEncoder
```

## What it works on

---

The plugin is reasonably advanced and supports transformation of complex data classes. 

Please see below the list of supported properties.

- Collection Properties
- Sealed Class properties
- Nested Data class properties
- Enum properties
- Value class properties
- Nullable properties

All the above is true recursively, Foo can contain a Bar which contains a Baz and the plugin will generate MapEncoders accordingly.

### Custom Types

For non data classes, or classes that simply hide part of their state internally you will need to give Synk a helping hand
in determining what values should be tracked.

If the class represents a single value, for example an Instant or UUID, provide a Synk Serializer by annotating an object

```kotlin
@SynkSerializer
object BarSerializer: StringSerializer<Bar> {
    override fun serialize(serializable: Bar): String {
        return serializable.test.toString()
    }

    override fun deserialize(serialized: String): Bar {
        return Bar(serialized.toInt())
    }
}
```

For classes that hold more than one value, you'll need to provide a MapEncoder

```kotlin
@SynkEncoder
class FooEncoder : MapEncoder<Foo> {
    override fun encode(crdt: Foo): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map["bar"] = crdt.hiddenBar()
        return map
    }

    override fun decode(map: Map<String, String>): Foo {
        val crdt = Foo()
        crdt.hiddenBar(map["bar"]!!)
        return crdt
    }
}
```


## How to use it

---

Setup KSP on the projects which contain the source code with annotated symbols

```kotlin
plugins {
    // Note you'll want to use a version > 1.8.0 for auto ide detection of generated outputs
    id("com.google.devtools.ksp") version "xxx"
}

dependencies {
    ksp("com.github.charlietap.synk:adapter-codegen:xxx")
}

//or in a KMP project

dependencies {
    add("kspCommonMainMetadata", "com.github.charlietap.synk:adapter-codegen:xxx")
    add("kspJvm", "com.github.charlietap.synk:adapter-codegen:xxx")
}
```

Create an IDResolver for the type you wish to generate a SynkAdapter and annotate the resolver with the @SynkAdapter annotation

```kotlin
@SynkAdapter
class FooResolver : IDResolver<Foo> {
    override fun resolveId(crdt: Foo): String {
        return crdt.bar
    }
}
```
