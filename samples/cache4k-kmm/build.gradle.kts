plugins {
    kotlin("multiplatform") version "1.5.0-RC" apply false
    id("com.android.application") version "4.1.3" apply false
    id("org.jetbrains.compose") version "0.4.0-build181" apply false
    id("io.gitlab.arturbosch.detekt") version "1.16.0"
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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
    dependencies.add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:1.16.0")
}
