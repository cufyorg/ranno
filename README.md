### Ranno - Runtime Annotation Processing [![](https://jitpack.io/v/org.cufy/ranno.svg)](https://jitpack.io/#org.cufy/ranno)

A KSP to shift annotation processing to runtime.

#### Why?

Annotation Processing in general is not appealing
for use in application code since, first,
enumerating annotated elements is near impossible
while, second, writing an annotation processor is
not a simple and straightforward task.

#### How?

The missing piece to achieve effective runtime
annotation processing is the ability to enumerate
annotated elements.

This feature is the purpose of this KSP.

First, the user defines a custom annotation
annotated by `@Enumerable`

Second, the user annotates elements with the
custom annotation.

Then, the KSP writes the signatures of the
annotated elements to the resources.

Meanwhile, the user uses `elementsWith()` to
enumerate the annotated elements.

Finally, at runtime, the code of `elementsWith()`
reads the signatures in the resources, obtain
runtime reflection instances, ensure elements are
actually annotated with the target annotation and
return the result.

#### Install

To add this KSP and its runtime to your project:

```kts
// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
```

```kts
// build.gradle.kts
plugins {
    // ...
    id("com.google.devtools.ksp") version "1.8.0-1.0.9"
}

repositories {
    // ...
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Replace TAG with the desired version
    ksp("org.cufy.ranno:ksp:TAG")

    implementation("org.cufy.ranno:runtime:TAG")
}

// To process @Enumerable annotations from different modules
ksp {
    arg("ranno.external", listOf(
        "org.cufy.ranno.Enumerated",
        "org.cufy.ranno.ktor.EnumeratedRoute",
        "org.cufy.ranno.ktor.EnumeratedApplication",
    ).joinToString(" "))
}
```

#### Usage

Define your custom annotation

```kotlin
@Enumerable
annotation class MyAnnotation(
    val value: String = ""
)
```

Annotate your elements

```kotlin
@MyAnnotation
fun hello() {
    println("Hello World")
}

@MyAnnotation
fun bye() {
    println("Bye World")
}
```

Enumerate your elements

```kotlin
fun main() {
    // to get a list of all kinds of annotated elements
    elementsWith<MyAnnotation>().forEach {
        println(it)
    }

    // a filter block can be passed
    elementsWith<MyAnnotation> { it.value == "" }

    // or a shortcut can be used
    functionsWith<MyAnnotation>()
    classesWith<MyAnnotation>()
    propertiesWith<MyAnnotation>()

    // to run the annotated elements safely 
    val r0 = runWith<MyAnnotation>(arg0, arg1, arg2 /* ... */)
    //  ^ the result of invoking all the elements in an unordered list

    // a filter block can be passed 
    val r1 = runWith<MyAnnotation>(arg0, arg1, arg2 /* ... */) {
        it.findAnnotations<MyAnnotation>()
            .any { it.value == "" }
    }

    // another way of running the annotated elements
    val r2 = applyWith<MyAnnotation>(arg1, arg2 /* ... */)
}
```
