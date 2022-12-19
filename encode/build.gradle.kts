@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinter)
}

kotlin {
    targets {
        jvm()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {

            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}