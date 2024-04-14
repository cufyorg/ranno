pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "ranno"

// include directories that starts with "ranno-"
for (file in rootDir.listFiles().orEmpty()) {
    if (file.isDirectory && file.name.startsWith("ranno-")) {
        include(":${file.name}")
    }
}
