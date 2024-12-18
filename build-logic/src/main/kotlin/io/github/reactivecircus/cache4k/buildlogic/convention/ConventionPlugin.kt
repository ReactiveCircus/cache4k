package io.github.reactivecircus.cache4k.buildlogic.convention

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.validation.BinaryCompatibilityValidatorPlugin
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
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
        extensions.configure<DokkaExtension> {
            dokkaPublications.configureEach {
                outputDirectory.set(layout.buildDirectory.dir("docs/api"))
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
        extensions.configure<KotlinMultiplatformExtension> {
            explicitApi()
            configureTargets(this@configureSubproject)
            sourceSets.configureEach {
                languageSettings.apply {
                    progressiveMode = true
                }
            }
        }
    }

    // configure detekt
    pluginManager.apply(DetektPlugin::class.java)
    dependencies.add("detektPlugins", the<LibrariesForLibs>().plugin.detektFormatting)
    plugins.withType<DetektPlugin> {
        extensions.configure<DetektExtension> {
            source.from(files("src/"))
            config.from(files("${project.rootDir}/detekt.yml"))
            buildUponDefaultConfig = true
            allRules = true
            parallel = true
        }
        tasks.withType<Detekt>().configureEach {
            jvmTarget = JvmTarget.JVM_11.target
            reports {
                xml.required.set(false)
                txt.required.set(false)
                sarif.required.set(false)
                md.required.set(false)
            }
        }
    }

    // configure test
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    // configure publishing
    pluginManager.apply(MavenPublishPlugin::class.java)
    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
        signAllPublications()
    }
}

@Suppress("LongMethod", "MagicNumber")
private fun KotlinMultiplatformExtension.configureTargets(project: Project) {
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
        vendor.set(JvmVendorSpec.AZUL)
    }
    project.tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.addAll(
                "-Xjvm-default=all"
            )
        }
    }
    project.tasks.withType<KotlinJsCompile>().configureEach {
        compilerOptions {
            moduleKind.set(JsModuleKind.MODULE_COMMONJS)
        }
    }
    jvm {
        val main = compilations.getByName("main")
        compilations.create("lincheck") {
            defaultSourceSet {
                dependencies {
                    implementation(main.compileDependencyFiles + main.output.classesDirs)
                }
            }
            project.tasks.register<Test>("jvmLincheck") {
                classpath = compileDependencyFiles + runtimeDependencyFiles + output.allOutputs
                testClassesDirs = output.classesDirs
                useJUnitPlatform()
                testLogging {
                    events("passed", "skipped", "failed")
                }
                jvmArgs(
                    "--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED",
                    "--add-opens", "java.base/java.lang=ALL-UNNAMED",
                    "--add-exports", "java.base/jdk.internal.util=ALL-UNNAMED",
                    "--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED",
                    "--add-exports", "java.base/jdk.internal.access=ALL-UNNAMED",
                )
            }
        }
    }
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
    macosX64()
    macosArm64()
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()
    watchosX64()
    watchosArm64()
    watchosSimulatorArm64()
    linuxX64()
    linuxArm64()
    mingwX64()
    applyDefaultHierarchyTemplate()

    @Suppress("UnusedPrivateProperty")
    sourceSets {
        val nonJvmMain by creating {
            dependsOn(commonMain.get())
        }
        val jvmLincheck by getting {
            dependsOn(jvmMain.get())
        }
        jsMain.get().dependsOn(nonJvmMain)
        val wasmJsMain by getting {
            dependsOn(nonJvmMain)
        }
        appleMain.get().dependsOn(nonJvmMain)
        linuxMain.get().dependsOn(nonJvmMain)
        mingwMain.get().dependsOn(nonJvmMain)
    }
}
