package io.github.reactivecircus.cache4k.buildlogic.convention

import kotlinx.validation.BinaryCompatibilityValidatorPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaPlugin

internal class RootPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        // configure dokka
        pluginManager.apply(DokkaPlugin::class.java)
        tasks.withType<DokkaMultiModuleTask>().configureEach {
            val apiDir = rootDir.resolve("docs/api")
            outputDirectory.set(apiDir)
            doLast {
                apiDir.resolve("-modules.html").renameTo(apiDir.resolve("index.html"))
            }
        }

        // configure compatibility validator
        pluginManager.apply(BinaryCompatibilityValidatorPlugin::class.java)
    }
}
