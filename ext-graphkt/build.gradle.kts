plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":runtime"))

    implementation("org.cufy:graphkt:2.0.0-beta.6")
}
