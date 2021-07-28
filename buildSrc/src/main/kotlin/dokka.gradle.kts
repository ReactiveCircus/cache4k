import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    id("org.jetbrains.dokka")
}

tasks.withType<DokkaMultiModuleTask>().configureEach {
    val apiDir = rootDir.resolve("docs/api")
    outputDirectory.set(apiDir)
    doLast {
        apiDir.resolve("-modules.html").renameTo(apiDir.resolve("index.html"))
    }
}
