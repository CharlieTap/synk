# Synk

![badge][badge-android]
![badge][badge-jvm]

---

A Kotlin multiplatform CRDT library for building offline/local first applications. 


Synk supercharges  client side databases to have distributed database properties such as:

- Conflict Resolution
- Causal Ordering

Allowing you to build offline first applications the way you want to, with the technologies you're familiar with.



# How does it work?

Synk is a state based CRDT library, it monitors state changes over time using a special type of [timestamp](https://github.com/CharlieTap/hlc)  which is capable 
of tracking events in a distributed system (your application). Synk maintains this data in its own persistent key value storage database locally on each client,
it's important to understand synk does not store your data, merely it stores timestamps associated with it.

In order for Synk to work, it needs to be informed of when state is created or updated in your application. It exposes two functions for this purpose:

```kotlin
Synk.outbound(new: T, old: T? = null): Message<T>
```

Whenever a new record/object is created or updated in your application locally, give synk the latest version and the old version (if applicable) and synk will return you a message.
This message needs to be propagated to all other clients.

```kotlin
Synk.inbound(message: Message<T>, old: T? = null): T 
```

When receiving a Message from another client application, inbound needs to be called. This function will perform conflict resolution for you and return an instance of your object ready to
be persisted.


# How should I architect my application using Synk

Synk is intentionally minimal and unopinionated in design, but there are of course some constraints that come from building an application with it.  
The two that stand out are the following:

- Messages must be relayed to all nodes in order for state to be consistent 
- Data exposed to Synk can never be deleted, at least not in the short term, soft deletes using tombstone fields is the recommended solution. 


Offline first applications that mutate state are distributed systems, there's no two ways about it. For this reason Synk has no concept of server like central storage.
Synk sees the world how any node in a distributed system would.

```mermaid
graph LR;
 NodeA[(NodeA)]
 NodeB[(NodeB)]
 NodeC[(NodeC)]
 NodeA--Message-->NodeB;
 NodeB--Message-->NodeC;
 NodeC--Message-->NodeA;
```



```mermaid
graph TD;
 Server--InboundMessage-->ClientA;
 Server--InboundMessage-->ClientB;
 ClientA--OutboundMessage-->Server;
 ClientB--OutboundMessage-->Server;
```


# API Usage

---
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

You'll need both Synk runtime and the delightful metastore artifacts to get started:

```kotlin
dependencies {
    implementation("com.github.charlietap.synk:delight-metastore:xxx")
    implementation("com.github.charlietap.synk:synk:xxx")
}
```
Alternatively if you're working with a KMP project you can pull the specialised dependencies for the different targets:

```kotlin
dependencies {
    implementation("com.github.charlietap.synk:synk-android:xxx")
    //or
    implementation("com.github.charlietap.synk:synk-jvm:xxx")
}
```

### State

Synk maintains two pieces of state in order to function:

- A logical clock, the location of this file is configured through ClockStorageConfiguration
- A key value database called the MetaStore, you can provide an instance of the DelightfulMetastoreFactory. This factory
uses Sqldelight under the hood and needs a Sqldriver to function. Depending on your platform you will need to provide the
appropriate driver, you can read how to do this [here](https://cashapp.github.io/sqldelight/1.5.4/multiplatform_sqlite/)

```kotlin
val clockStorageConfig = ClockStorageConfiguration(
    filePath = "/synk".toPath(),
    fileSystem = FileSystem.SYSTEM
)
val factory = DelightfulMetastoreFactory(driver)
val synk = Synk.Builder(clockStorageConfig)
    .metaStoreFactory(factory)
    .build()
```

If you're on Android a preset extension function exists for the builder which configures the clock storage configuration for you:

```kotlin
val synk = Synk.Builder.Presets.Android(context)
.metaStoreFactory(metastoreFactory)
.build()
```


### Synk Adapters

Synk adapters tell synk how to serialize and deserialize types into generic maps it can perform conflict resolution on. For 
every type T you intend to use with Synk you must provide a SynkAdapter<T> when constructing your synk instance.

```kotlin
val synk = Synk.Builder(...)
    .registerSynkAdapter(adapter)
    .registerSynkAdapter(adapter2)
    .build()
```

For more information on Synk adapters please visit the [documentation page](docs/synk-adapters.md)

## Conflict Resolution, Ordering and Messages

Synk resolves conflicts by recording a causal order of events in the Metastore, it groups events into two categories.

Local events are events that occur on the current node (client application), notify Synk of the event by passing the new
and old (in the case of updates) to outbound once they have already been persisted to the database.  

Synk will return you a Message, it's your responsibility to ensure all nodes receive all messages relevant to them.

```kotlin
Synk.outbound(new: T, old: T? = null): Message<T>
```

`inbound` is the ying to `outbound`'s yang, and the destination for the Messages `outbound` creates. The result of inbound is an object ready to be inserted into a database, free of conflicts and consistent across all nodes. 


```kotlin
Synk.inbound(message: Message<T>, old: T? = null): T 
```

## Change Detection

Coming soon ...

## Serialization

To aid the relay of messages between applications Synk provides methods for serializing Messages to and from json.
These serializers make use of the Synk adapters provided and tend to have better performance than reflections powered
serializers like gson.

```kotlin
Synk.serialize(messages: List<Message<T>>): String
```

```kotlin
Synk.deserialize(encoded: String): List<Message<T>>
```

For those looking to serialize messages themselves, Synk exposes Message/Meta Serializers for popular libs:

- [Kotlin Serialization](docs/extensions.md#kotlinx-serialization)

## Compaction

Messages generated by Synk are commutative, associative and idempotent. This means that you can combine the messages in any order you want,
however many times as you want, switching which messages combine with each other, and you will always deterministically get the same result.

This affords us the ability to merge a series of messages for a particular object into just one, it's latest state. You could for example batch all the messages
regarding a particular object whilst offline, then compact all the changes into one Message before relaying this information to other nodes.

Taking this idea further you could store all the Messages ever created in an Event Store, and compact them to derive the current state of the system.


```kotlin
Synk.compact(messages: List<Message<T>>): List<Message<T>> 
```

## Testing

Synk is designed from the ground up to be testable, by default Synk will use in memory metastore for metadata persistence.  
You can easily configure a test friendly instance as follows:

```kotlin
val storageConfiguration = StorageConfiguration(
    filePath = "/test".toPath(),
    fileSystem = FakeFileSystem()
)

val testSynk = Synk.Builder(storageConfiguration)
    .build()
```




[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat
[badge-js]: http://img.shields.io/badge/-js-F8DB5D.svg?style=flat
[badge-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg?style=flat
[badge-windows]: http://img.shields.io/badge/-windows-4D76CD.svg?style=flat
[badge-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat
[badge-mac]: http://img.shields.io/badge/-macos-111111.svg?style=flat
