rootProject.name = "cache4k"
include(":cache4k")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "binary-compatibility-validator") {
                useModule("org.jetbrains.kotlinx:binary-compatibility-validator:${requested.version}")
            }
        }
    }
}
