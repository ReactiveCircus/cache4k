pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal {
            content {
                includeGroupByRegex("org.gradle.*")
            }
        }
        mavenCentral()
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

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

rootProject.name = "cache4k"
include(":cache4k")
