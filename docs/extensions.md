# Synkx - Synk Extensions

---

Synk extensions are additional libraries which can make integration with other technologies/libraries easier 

## Kotlinx-coroutines

- Coming soon


## Kotlinx-serialization

This extension exposes kotlinx serializers for the following types:

- Message using [MessageSerializer](../extension/kotlin-serialization/src/commonMain/kotlin/com/tap/synk/extension/MessageSerializer.kt)
- Meta using [MetaSerializer](../extension/kotlin-serialization/src/commonMain/kotlin/com/tap/synk/extension/MetaSerializer.kt)

## Setup

### Gradle

Synk currently uses Jitpack to distribute artifacts, you'll need to ensure that the jitpack maven repo is configured
in you dependency resolution management block.

```kotlin
dependencyResolutionManagement {
    repositories {
        ...
        maven(url = "https://jitpack.io" )
```

Register the following dependency:

```kotlin
dependencies {
    implementation("com.github.charlietap.synk:kotlin-serialization:xxx")
}
```

### Usage

You can use the serializers directly, for example 

> Note that the message serializer needs to be given the serializer for its inner type T

<!--- INCLUDE
import kotlinx.serialization.*
import kotlinx.serialization.json.*
-->

```kotlin
val json = Json.encodeToString(MessageSerializer(Foo.serializer()), message)
```

You can specify the serializer on a property 

```kotlin
object MessageFooSerializer : KSerializer<Message<Foo>> {
    private val delegateSerializer = MessageSerializer(Foo.serializer())
    override val descriptor = SerialDescriptor("Message", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Message<Foo>) {
        encoder.encodeSerializableValue(delegateSerializer, value)
    }

    override fun deserialize(decoder: Decoder): Message<Foo> {
        return decoder.decodeSerializableValue(delegateSerializer)
    }
}

@Serializable
data class Bar(
    @Serializable(with = MessageFooSerializer::class)
    val message: Message<Foo>,
)
```

You'll more than likely want to serialize lists of messages, this isn't currently possible by annotating a property,
instead you'll have to register the custom serializer on the file that requires it. For example:


```kotlin
object MessageFooSerializer : KSerializer<Message<Foo>> {
    private val delegateSerializer = MessageSerializer(Foo.serializer())
    override val descriptor = SerialDescriptor("Message", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Message<Foo>) {
        encoder.encodeSerializableValue(delegateSerializer, value)
    }

    override fun deserialize(decoder: Decoder): Message<Foo> {
        return decoder.decodeSerializableValue(delegateSerializer)
    }
}

@file:UseSerializers(MessageFooSerializer::class)

@Serializable
data class Bar(
    val messages: List<Message<Foo>>,
)
```

