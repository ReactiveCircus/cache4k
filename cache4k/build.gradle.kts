plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

group = property("GROUP") as String
version = property("VERSION_NAME") as String

kotlin {
    explicitApi()
    jvm {
        compilations.all {
            kotlinOptions {
                kotlinOptions.jvmTarget = "1.8"
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-Xjvm-default=all"
                )
            }
        }
    }
    js(BOTH) {
        compilations.all {
            kotlinOptions {
                moduleKind = "commonjs"
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

    sourceSets {
        all {
            languageSettings.apply {
                languageVersion = "1.6"
                progressiveMode = true
                enableLanguageFeature("NewInference")
                optIn("kotlin.Experimental")
                optIn("kotlin.time.ExperimentalTime")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.stately.concurrency)
                implementation(libs.stately.isolate)
                implementation(libs.stately.isoCollections)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.testCommon)
                implementation(libs.kotlin.testAnnotationsCommon)
                implementation(libs.coroutines.test)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.testJUnit)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(libs.kotlin.testJS)
            }
        }
    }
}
