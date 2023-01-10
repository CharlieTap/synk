@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.lib)
    alias(libs.plugins.sqldelight.legacy)
    alias(libs.plugins.kotlinter)
    id("maven-publish")
}
group = "com.tap.synk"
version = libs.versions.version.name.get()

sqldelight {
    database("DelightfulDatabase") {
        packageName = "com.tap.delight.metastore"
    }
}

kotlin {

    targets {
        jvm()
        android()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.runtime)
                implementation(projects.encode)
                implementation(projects.concurrentMap)
                implementation(libs.murmurhash)
                implementation(libs.androidx.collections.kmp)
                implementation(libs.uuid)
                api(libs.sqldelight.jvm.driver.legacy)

            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.sqldelight.jvm.driver.legacy)
            }
        }
    }
}

android {
    namespace = "com.tap.delight.metastore"
    compileSdk = libs.versions.compile.sdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    androidComponents {
        beforeVariants { builder ->
            if(builder.buildType == "debug") {
                builder.enable = false
            } else {
                builder.enableUnitTest = false
                builder.enableAndroidTest = false
            }
        }
    }
}