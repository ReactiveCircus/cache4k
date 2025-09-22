import dev.detekt.gradle.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
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
    source.from(files("src/"))
    config.from(files("../detekt.yml"))
    buildUponDefaultConfig = true
    parallel = true
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JvmTarget.JVM_11.target
    reports {
        xml.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

gradlePlugin {
    plugins {
        register("convention") {
            id = "cache4k.convention"
            implementationClass = "io.github.reactivecircus.cache4k.buildlogic.convention.ConventionPlugin"
        }
    }
}

dependencies {
    // TODO: remove when this fixed
    //  https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    // enable Ktlint formatting
    add("detektPlugins", libs.plugin.detektKtlintWrapper)

    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.dokka)
    implementation(libs.plugin.binaryCompatibilityValidator)
    implementation(libs.plugin.detekt)
    implementation(libs.plugin.mavenPublish)
}
