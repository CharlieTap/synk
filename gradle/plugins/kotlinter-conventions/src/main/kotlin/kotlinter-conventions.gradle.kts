import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
   id("org.jmailen.kotlinter")
}

kotlinter {

}

tasks.withType<LintTask>().configureEach {
    exclude { it.file.path.contains("/build/".toRegex()) }
}
