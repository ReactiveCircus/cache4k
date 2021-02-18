val statelyVersion = "1.1.3"
val statelyIsoVersion = "1.1.3-a1"
val coroutinesVersion = "1.4.2"

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
    id("binary-compatibility-validator")
}

group = property("GROUP") as String
version = property("VERSION_NAME") as String

kotlin {
    explicitApi()
    jvm {
        compilations.all {
            kotlinOptions {
                useIR = true
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
    watchosArm32()
    watchosArm64()
    tvosX64()
    tvosArm64()

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
                implementation("co.touchlab:stately-concurrency:$statelyVersion")
                implementation("co.touchlab:stately-isolate:$statelyIsoVersion")
                implementation("co.touchlab:stately-iso-collections:$statelyIsoVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
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
        val nativeTest by creating {
            dependsOn(commonTest)
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
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
        val watchosArm32Test by getting {
            dependsOn(nativeTest)
        }
        val watchosArm64Test by getting {
            dependsOn(nativeTest)
        }
        val tvosX64Test by getting {
            dependsOn(nativeTest)
        }
        val tvosArm64Test by getting {
            dependsOn(nativeTest)
        }
    }
}
