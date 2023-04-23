plugins {
    id("cache4k.library")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.stately.isoCollections)
                implementation(libs.atomicfu)
            }
        }
        val jvmAndIosMain by getting {
            dependencies {
                dependsOn(commonMain)
                implementation(libs.androidx.collection)
            }
        }
        val unixDesktopMain by getting {
            dependencies {
                dependsOn(commonMain)
                implementation(libs.androidx.collection)
            }
        }
        val nonIosAppleMain by getting {
            dependencies {
                dependsOn(commonMain)
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
