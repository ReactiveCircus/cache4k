import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    id("org.jetbrains.dokka")
}

tasks.withType<DokkaMultiModuleTask>().configureEach {
    val apiDir = rootDir.resolve("docs/api")
    outputDirectory.set(apiDir)
}
