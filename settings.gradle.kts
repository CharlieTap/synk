pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    includeBuild("gradle/plugins/kotlinter-conventions")
    includeBuild("gradle/plugins/versions-conventions")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://jitpack.io" )

    }
}

include(":libs:concurrent-map")
include(":synk")
include(":extension:kotlin-serialization")
include(":metastores:delightful-metastore")

rootProject.name = "synk-multiplatform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")