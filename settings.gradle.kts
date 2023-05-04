pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "ranno"

include(":ksp")
include(":runtime")
include(":example")
include(":ext-ktor")
include(":ext-graphkt")
include(":ext-clikt")
