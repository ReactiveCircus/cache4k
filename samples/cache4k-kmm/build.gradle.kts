plugins {
    kotlin("multiplatform") version "1.4.21" apply false
    id("com.android.application") version "4.0.2" apply false
    id("org.jetbrains.compose") version "0.3.0-build139" apply false
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
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
        failFast = true
        config = files("${project.rootDir}/detekt.yml")
        buildUponDefaultConfig = true
        reports {
            html.destination = file("${project.buildDir}/reports/detekt/${project.name}.html")
        }
    }
    dependencies.add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:1.15.0")
}
