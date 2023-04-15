import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

group = property("GROUP") as String
version = property("VERSION_NAME") as String

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
    signAllPublications()
}

kotlin {
    explicitApi()
    jvm {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.addAll(
                    "-Xjvm-default=all"
                )
            }
        }
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
            vendor.set(JvmVendorSpec.AZUL)
        }
    }
    js(IR) {
        compilations.all {
            compilerOptions.configure {
                moduleKind.set(JsModuleKind.MODULE_COMMONJS)
            }
        }
        browser()
        nodejs()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    watchosArm64()
    watchosSimulatorArm64()
    watchosX64()
    linuxX64()
    mingwX64()

    sourceSets {
        all {
            languageSettings.apply {
                languageVersion = "1.8"
                progressiveMode = true
                enableLanguageFeature("NewInference")
                optIn("kotlin.time.ExperimentalTime")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.stately.isoCollections)
                implementation(libs.atomicfu)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.coroutines.test)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
