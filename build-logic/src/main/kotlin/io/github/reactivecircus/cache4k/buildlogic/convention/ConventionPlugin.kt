package io.github.reactivecircus.cache4k.buildlogic.convention

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektPlugin
import kotlinx.validation.BinaryCompatibilityValidatorPlugin
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal class ConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        // apply dokka plugin
        pluginManager.apply(DokkaPlugin::class.java)

        if (rootProject == this) {
            configureRootProject()
        } else {
            configureSubproject()
        }
    }
}

private fun Project.configureRootProject() {
    plugins.withId("org.jetbrains.dokka") {
        extensions.configure(DokkaExtension::class.java) {
            it.dokkaPublications.configureEach {
                it.outputDirectory.set(layout.buildDirectory.dir("docs/api"))
            }
        }
    }

    // configure compatibility validator
    pluginManager.apply(BinaryCompatibilityValidatorPlugin::class.java)
}

private fun Project.configureSubproject() {
    // configure KMP library project
    pluginManager.apply("org.jetbrains.kotlin.multiplatform")
    group = property("GROUP") as String
    version = property("VERSION_NAME") as String

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            it.explicitApi()
            it.configureTargets(this@configureSubproject)
            it.sourceSets.configureEach {
                it.languageSettings.apply {
                    progressiveMode = true
                }
            }
        }
    }

    // configure detekt
    pluginManager.apply(DetektPlugin::class.java)
    dependencies.add("detektPlugins", (extensions.getByName("libs") as LibrariesForLibs).plugin.detektKtlintWrapper)
    plugins.withType(DetektPlugin::class.java) {
        extensions.configure(DetektExtension::class.java) {
            it.source.from(files("src/"))
            it.config.from(files("${project.rootDir}/detekt.yml"))
            it.buildUponDefaultConfig.set(true)
            it.parallel.set(true)
        }
        tasks.withType(Detekt::class.java).configureEach {
            it.jvmTarget.set(JvmTarget.JVM_17.target)
            it.reports { report ->
                report.checkstyle.required.set(false)
                report.sarif.required.set(false)
                report.markdown.required.set(false)
            }
        }
    }

    // configure test
    tasks.withType(Test::class.java).configureEach { test ->
        test.useJUnitPlatform()
        test.testLogging {
            it.events("passed", "skipped", "failed")
        }
    }

    // configure publishing
    pluginManager.apply(MavenPublishPlugin::class.java)
    extensions.configure(MavenPublishBaseExtension::class.java) {
        it.publishToMavenCentral(automaticRelease = true)
        it.signAllPublications()
    }
}

@Suppress("LongMethod", "MagicNumber")
private fun KotlinMultiplatformExtension.configureTargets(project: Project) {
    targets.configureEach {
        it.compilations.configureEach {
            it.compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }
    project.tasks.withType(KotlinJvmCompile::class.java).configureEach {
        it.compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    project.tasks.withType(KotlinJsCompile::class.java).configureEach {
        it.compilerOptions {
            moduleKind.set(JsModuleKind.MODULE_COMMONJS)
        }
    }
    jvm()
    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    tvosArm64()
    tvosSimulatorArm64()
    watchosArm64()
    watchosSimulatorArm64()
    linuxX64()
    linuxArm64()
    mingwX64()
    applyDefaultHierarchyTemplate()

    with(sourceSets) {
        create("nonJvmMain") {
            it.dependsOn(commonMain.get())
        }
        jsMain.get().dependsOn(getByName("nonJvmMain"))
        getByName("wasmJsMain") {
            it.dependsOn(getByName("nonJvmMain"))
        }
        appleMain.get().dependsOn(getByName("nonJvmMain"))
        linuxMain.get().dependsOn(getByName("nonJvmMain"))
        mingwMain.get().dependsOn(getByName("nonJvmMain"))
    }
}
