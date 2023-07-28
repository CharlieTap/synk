import com.android.build.api.dsl.SettingsExtension

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    includeBuild("gradle/plugins/kotlinter-conventions")
    includeBuild("gradle/plugins/versions-conventions")
}

plugins {
    id("com.gradle.enterprise") version ("3.13.4")
    id("com.android.settings")  version("8.0.0")
}

configure<SettingsExtension> {
    compileSdk = 32
    minSdk = 24
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"

        publishAlwaysIf(!System.getenv("GITHUB_ACTIONS").isNullOrEmpty())
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

include(":libs:annotations")
include(":libs:concurrent-map")
include(":synk")
include(":extension:kotlin-serialization")
include(":metastores:delightful-metastore")
include(":plugins:ksp-adapter-codegen")

rootProject.name = "synk-multiplatform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
