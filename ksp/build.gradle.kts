plugins {
    kotlin("jvm")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":runtime"))
    implementation(libs.ksp)
}
