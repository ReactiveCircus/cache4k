val detektVersion = "1.15.0"

plugins {
    kotlin("multiplatform") version "1.4.30-M1" apply false
    id("org.jetbrains.dokka") version "1.4.10"
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        input = files("src/")
        failFast = true
        config = files("${project.rootDir}/detekt.yml")
        buildUponDefaultConfig = true
        reports {
            html.destination = file("${project.buildDir}/reports/detekt/${project.name}.html")
        }
    }
    dependencies.add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:${detektVersion}")
}
