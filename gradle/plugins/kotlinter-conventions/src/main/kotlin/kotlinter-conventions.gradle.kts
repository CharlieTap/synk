import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
   id("org.jmailen.kotlinter")
}

kotlinter {
    disabledRules = arrayOf("filename","multiline-if-else")
}

tasks.withType<LintTask>().configureEach {
    exclude { it.file.path.contains("/build/".toRegex()) }
}