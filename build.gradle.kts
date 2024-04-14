plugins {
    kotlin("jvm") version libs.versions.kotlin apply false
    id("maven-publish")
}

group = "org.cufy"
version = "1.0.0"

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.20"))
    }
}

tasks.wrapper {
    gradleVersion = "8.2.1"
}

subprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }

    if (name == "ranno-example") return@subprojects

    group = "org.cufy.ranno"

    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    from(components["java"])
                    artifactId = project.name
                }
            }
        }
    }
}
