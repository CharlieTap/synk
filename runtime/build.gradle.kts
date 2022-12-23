@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomic.fu)
    alias(libs.plugins.kotlinter)
}

group = "com.tap.synk"
version = "0.1"

kotlin {

    targets.all {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = false
                freeCompilerArgs += listOf("-Xcontext-receivers")
            }
        }
    }

    targets {
        jvm()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.concurrentMap)
                implementation(projects.encode)
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
            }
        }

        val jvmMain by getting {
            dependencies {

            }
        }

        val nativeMain by creating {
            dependencies {

            }
        }
    }
}