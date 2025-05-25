plugins {
    kotlin("jvm")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":ranno-runtime"))
    implementation(libs.ksp.api)
    implementation(libs.ksp.aa)
    implementation(libs.ksp.aaEmbeddable)
    implementation(libs.ksp.commonDeps)
}
