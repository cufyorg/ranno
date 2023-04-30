plugins {
    kotlin("jvm") version "1.8.0" apply false
    id("maven-publish")
}

group = "org.cufy"
version = "1.0.0"

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.8.0"))
    }
}

subprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }

    if (name == "example") return@subprojects

    group = "org.cufy.graphkt"

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
