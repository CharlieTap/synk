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

//rootDir.listFiles()?.filter {
//    File(it, "build.gradle.kts").exists()
//}?.forEach {
//    include(it.name)
//}

include(":api")
include(":encode")
include(":runtime")
include(":delight-metastore")

rootProject.name = "synk"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")