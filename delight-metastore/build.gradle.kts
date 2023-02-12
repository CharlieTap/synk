import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.build.version.get().toInt()))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }

    targets {
        jvm()
        android()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.synk)
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
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.bytecode.version.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.bytecode.version.get().toInt())
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

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = libs.versions.java.bytecode.version.get()
}
