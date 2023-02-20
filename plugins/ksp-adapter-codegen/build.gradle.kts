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

    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.kotlin.junit)
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.9")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.9")

    ksp(libs.autoservice.ksp)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
        )
    }
}
