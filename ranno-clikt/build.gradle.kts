plugins {
    kotlin("jvm")
    id("maven-publish")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":ranno-runtime"))

    implementation("com.github.ajalt.clikt:clikt:3.5.2")

    ksp(project(":ranno-ksp"))
}
