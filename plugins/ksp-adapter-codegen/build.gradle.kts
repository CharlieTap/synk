import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.symbol.processing)
}

ksp {
    arg("autoserviceKsp.verify", "true")
    arg("autoserviceKsp.verbose", "true")
}

dependencies {
    implementation(projects.synk)
    implementation(libs.kotlin.poet.core)
    implementation(libs.kotlin.poet.ksp)
    implementation(libs.kotlin.symbol.processing.api)
    implementation(libs.autoservice.annotations)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.junit)
    testImplementation(libs.kotlin.compile.testing.core)
    testImplementation(libs.kotlin.compile.testing.ksp)

    ksp(libs.autoservice.ksp)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(libs.versions.java.bytecode.version.get().toInt())
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.bytecode.version.get()))
        freeCompilerArgs.addAll(
            "-Xcontext-receivers"
        )
    }
}
