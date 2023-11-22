import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    id("kotlinter-conventions")
    id("maven-publish")
}
group = "com.tap.synk.extension"
version = libs.versions.version.name.get()

kotlin {

    jvm()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.synk)
                implementation(libs.kotlinx.serialization)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = libs.versions.java.bytecode.version.get()
}
