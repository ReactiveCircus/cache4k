val detektVersion = "1.13.1"

plugins {
    id("org.jetbrains.dokka") version "1.4.0"
    id("io.gitlab.arturbosch.detekt") version "1.13.1"
}

allprojects {
    repositories {
        mavenCentral()
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
