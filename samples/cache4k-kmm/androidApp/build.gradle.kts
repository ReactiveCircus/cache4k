plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.runtime)
    implementation(compose.ui)
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId = "io.github.reactivecircus.cache4k.androidApp"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
