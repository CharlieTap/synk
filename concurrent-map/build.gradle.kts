@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.kotlin.atomic.fu)
    id("maven-publish")
}
group = "com.tap.synk"
version = libs.versions.version.name.get()


kotlin {
    targets {
        jvm()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.atomic.fu)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}