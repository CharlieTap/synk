import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.lib)
    alias(libs.plugins.sqldelight.legacy)
    id("kotlinter-conventions")
    id("maven-publish")
}
group = "com.tap.delight.metastore"
version = libs.versions.version.name.get()

sqldelight {
    database("DelightfulDatabase") {
        packageName = "com.tap.delight.metastore"
    }
}

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }

    targets {
        jvm()
        androidTarget()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.synk)
                implementation(projects.libs.concurrentMap)
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
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.bytecode.version.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.bytecode.version.get().toInt())
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
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
