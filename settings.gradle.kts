rootProject.name = "cache4k"
include(":cache4k")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "binary-compatibility-validator") {
                useModule("org.jetbrains.kotlinx:binary-compatibility-validator:${requested.version}")
            }
        }
    }

    val toolchainsResolverVersion = file("$rootDir/gradle/libs.versions.toml")
        .readLines()
        .first { it.contains("toolchainsResolver") }
        .substringAfter("=")
        .trim()
        .removeSurrounding("\"")

    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version toolchainsResolverVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}
