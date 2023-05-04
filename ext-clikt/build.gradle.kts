plugins {
    kotlin("jvm")
    id("maven-publish")
    id("com.google.devtools.ksp") version "1.8.0-1.0.9"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":runtime"))

    implementation("com.github.ajalt.clikt:clikt:3.5.2")
    ksp(project(":ksp"))
}
