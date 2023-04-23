import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}

detekt {
    source = files("src/")
    config = files("../detekt.yml")
    buildUponDefaultConfig = true
    allRules = true
    parallel = true
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JvmTarget.JVM_11.target
    reports {
        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

gradlePlugin {
    plugins {
        register("root") {
            id = "cache4k.root"
            implementationClass = "io.github.reactivecircus.cache4k.buildlogic.convention.RootPlugin"
        }
        register("library") {
            id = "cache4k.library"
            implementationClass = "io.github.reactivecircus.cache4k.buildlogic.convention.LibraryConventionPlugin"
        }
    }
}

dependencies {
    // TODO: remove when this fixed
    //  https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    // enable Ktlint formatting
    add("detektPlugins", libs.plugin.detektFormatting)

    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.dokka)
    implementation(libs.plugin.binaryCompatibilityValidator)
    implementation(libs.plugin.detekt)
    implementation(libs.plugin.mavenPublish)
}
