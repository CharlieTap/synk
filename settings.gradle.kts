pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://jitpack.io" )

    }
}

//toolchainManagement {
//    jvm {
//        javaRepositories {
//            repository("adoptium") {
//                resolverClass.set(AdoptiumResolver::class.java)
//            }
//        }
//    }
//}

//rootDir.listFiles()?.filter {
//    File(it, "build.gradle.kts").exists()
//}?.forEach {
//    include(it.name)
//}

include(":concurrent-map")
include(":synk")
include(":extension:kotlin-serialization")
include(":delight-metastore")

rootProject.name = "synk-multiplatform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")