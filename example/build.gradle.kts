plugins {
    kotlin("kapt")
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.8.0-1.0.9"
}

ksp {
    arg("ranno.external", listOf(
        "org.cufy.ranno.Enumerated",
        "org.cufy.ranno.ktor.EnumeratedRoute",
        "org.cufy.ranno.ktor.EnumeratedApplication",
    ).joinToString(" "))
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(kotlin("test"))
    implementation(project(":runtime"))

    ksp(project(":ksp"))
}
