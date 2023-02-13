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

include(":libs:concurrent-map")
include(":synk")
include(":extension:kotlin-serialization")
include(":metastores:delightful-metastore")

rootProject.name = "synk-multiplatform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")