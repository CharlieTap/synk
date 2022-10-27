@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
}

group = "com.tap"
version = "1.0-SNAPSHOT"

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
                api(libs.kotlin.reflection)
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.serialization)
                api(libs.hlc)
                api(libs.uuid)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}