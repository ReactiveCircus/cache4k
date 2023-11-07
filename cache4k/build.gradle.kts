plugins {
    id("cache4k.convention")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.stately.isoCollections)
                implementation(libs.atomicfu)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.coroutines.test)
            }
        }
        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
        jvmLincheck {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation(libs.lincheck)
            }
        }
        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
