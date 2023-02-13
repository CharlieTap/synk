import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.lib)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomic.fu)
    alias(libs.plugins.kotlinter)
    id("maven-publish")
}

group = "com.tap.synk"
version = libs.versions.version.name.get()


kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.build.version.get().toInt()))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }

    android {
        publishLibraryVariants("release")
    }

    targets.configureEach {
        compilations.configureEach {
            kotlinOptions {
                allWarningsAsErrors = false
                freeCompilerArgs += listOf("-Xcontext-receivers")
            }
        }
    }

    targets {
        android()
        jvm()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.libs.concurrentMap)
                implementation(libs.kotlin.reflection)
                implementation(libs.kotlinx.atomic.fu)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.hlc)
                implementation(libs.uuid)
                api(libs.okio)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.okio.fakefilesystem)
                implementation(libs.faker)
            }
        }

        val jvmMain by getting {
            dependencies {

            }
        }

//        val nativeMain by creating {
//            dependencies {
//
//            }
//        }
    }
}

android {

    namespace = "com.tap.synk"
    compileSdk = libs.versions.compile.sdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.bytecode.version.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.bytecode.version.get().toInt())
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        warningsAsErrors = false
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