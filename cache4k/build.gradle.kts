plugins {
    id("cache4k.convention")
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
        val nonJvmMain by getting {
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
                implementation(kotlin("test-junit5"))
            }
        }
        val jvmLincheck by getting {
            dependencies {
                dependsOn(commonMain)
                implementation(kotlin("test-junit5"))
                implementation(libs.lincheck)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
