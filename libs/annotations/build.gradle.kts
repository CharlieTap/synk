import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.atomic.fu)
    id("kotlinter-conventions")
    id("maven-publish")
}
group = "com.tap.synk.lib"
version = libs.versions.version.name.get()

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }

    targets {
        jvm()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {}
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = libs.versions.java.bytecode.version.get()
}