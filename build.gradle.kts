val detektVersion = "1.17.1"

plugins {
    kotlin("multiplatform") version "1.5.20" apply false
    id("com.vanniktech.maven.publish") version "0.16.0" apply false
    id("org.jetbrains.dokka") version "1.4.32"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    id("binary-compatibility-validator") version "0.6.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

tasks.dokkaHtmlMultiModule.configure {
    val apiDir = rootDir.resolve("docs/api")
    outputDirectory.set(apiDir)
    doLast {
        apiDir.resolve("-modules.html").renameTo(apiDir.resolve("index.html"))
    }
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        input = files("src/")
        config = files("${project.rootDir}/detekt.yml")
        buildUponDefaultConfig = true
        allRules = true
        reports {
            html.destination = file("${project.buildDir}/reports/detekt/${project.name}.html")
        }
    }
    dependencies.add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:${detektVersion}")
}
