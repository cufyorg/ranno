val kspVersion: String by project

plugins {
    kotlin("jvm")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":runtime"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
}
