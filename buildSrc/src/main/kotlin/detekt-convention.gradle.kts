import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val detektVersion = the<LibrariesForLibs>().versions.detekt.get()

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        source = files("src/")
        config = files("${project.rootDir}/detekt.yml")
        buildUponDefaultConfig = true
        allRules = true
    }
    tasks.withType<Detekt>().configureEach {
        jvmTarget = "11"
        reports {
            html.outputLocation.set(file("build/reports/detekt/${project.name}.html"))
        }
    }
    dependencies.add(
        "detektPlugins",
        "io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion",
    )
}
