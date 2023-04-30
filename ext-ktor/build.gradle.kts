plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":runtime"))

    implementation("io.ktor:ktor-server-core:2.3.0")
}
