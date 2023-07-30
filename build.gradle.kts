@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android) apply false
    alias(libs.plugins.android.lib) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.atomic.fu) apply false
    alias(libs.plugins.kotlin.symbol.processing) apply false
    alias(libs.plugins.sqldelight) apply false

    id("versions-conventions")

//    alias(libs.plugins.dependency.analysis)
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}
