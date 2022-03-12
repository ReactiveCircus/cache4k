plugins {
    `kotlin-dsl`
}

dependencies {
    // TODO: remove when this fixed
    //  https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.plugin.detekt)
    implementation(libs.plugin.dokka)
    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.kotlinBinaryCompatibilityValidator)
    implementation(libs.plugin.mavenPublish)
}
