import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.5.21" apply false
    id("com.android.application") version "7.0.0" apply false
    id("org.jetbrains.compose") version "0.5.0-build262" apply false
    id("io.gitlab.arturbosch.detekt") version "1.18.0"
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xopt-in=kotlin.RequiresOptIn"
            )
        }
    }
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        source = files("src/")
        config = files("${project.rootDir}/detekt.yml")
        buildUponDefaultConfig = true
        allRules = true
        reports {
            html.destination = file("${project.buildDir}/reports/detekt/${project.name}.html")
        }
    }
    dependencies.add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:1.17.1")
}
