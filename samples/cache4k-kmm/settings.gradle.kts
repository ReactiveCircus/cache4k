rootProject.name = "cache4k-kmm"
include(":androidApp")
include(":shared")

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
            if (requested.id.id == "org.jetbrains.compose") {
                useModule("org.jetbrains.compose:compose-gradle-plugin:${requested.version}")
            }
        }
    }
}
