plugins {
    kotlin("jvm")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":ranno-runtime"))
    implementation(libs.ksp)
}
