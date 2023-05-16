plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":runtime"))

    implementation("org.cufy.graphkt:core:2.0.0-beta.7")
    implementation("org.cufy.graphkt:ktor:2.0.0-beta.7")
}
