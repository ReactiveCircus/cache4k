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
    macosX64()

    sourceSets {
        all {
            languageSettings.apply {
                progressiveMode = true
                enableLanguageFeature("NewInference")
                useExperimentalAnnotation("kotlin.Experimental")
                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
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
                implementation(libs.coroutines.core)
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
        val nativeTest by creating {
            dependsOn(commonTest)
            dependencies {
                implementation(libs.coroutines.core)
            }
        }
        val iosX64Test by getting {
            dependsOn(nativeTest)
        }
        val iosArm64Test by getting {
            dependsOn(nativeTest)
        }
        val macosX64Test by getting {
            dependsOn(nativeTest)
        }
    }
}
