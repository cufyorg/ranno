package org.cufy.ranno.example.alpha

import kotlinx.coroutines.runBlocking
import org.cufy.ranno.Enumerable
import org.cufy.ranno.elementsWith
import kotlin.reflect.KCallable
import kotlin.reflect.full.callSuspend

const val TOPLEVEL_SUSPEND_FUNCTION = "Toplevel Suspend Function"

const val TOPLEVEL_FUNCTION = "Toplevel Function"
const val TOPLEVEL_EXTENSION_FUNCTION = "Toplevel Extension Function"

const val TOPLEVEL_PROPERTY = "Toplevel Property"
const val TOPLEVEL_PROPERTY_NO_FIELD = "Toplevel Property (No Field)"
const val TOPLEVEL_EXTENSION_PROPERTY = "Toplevel Extension Property"
const val TOPLEVEL_DELEGATED_PROPERTY = "Toplevel Delegated Property"
const val TOPLEVEL_DELEGATED_EXTENSION_PROPERTY = "Toplevel Delegated Extension Property"

const val MEMBER_SUSPEND_FUNCTION = "Member Suspend Function"

const val MEMBER_FUNCTION = "Member Function"
const val MEMBER_EXTENSION_FUNCTION = "Member Extension Function"

const val MEMBER_PROPERTY = "Member Property"
const val MEMBER_PROPERTY_NO_FIELD = "Member Property (No Field)"
const val MEMBER_EXTENSION_PROPERTY = "Member Extension Property"
const val MEMBER_DELEGATED_PROPERTY = "Member Delegated Property"
const val MEMBER_DELEGATED_EXTENSION_PROPERTY = "Member Delegated Extension Property"

val EXPECTED_NAMES = listOf(
    "toplevelSuspendFunction",
    "toplevelFunction",
    "toplevelExtensionFunction",
    "toplevelProperty",
    "toplevelPropertyNoField",
    "toplevelExtensionProperty",
    "toplevelDelegatedProperty",
    "toplevelDelegatedExtensionProperty",
    "memberSuspendFunction",
    "memberFunction",
    "memberExtensionFunction",
    "memberProperty",
    "memberPropertyNoField",
    "memberExtensionProperty",
    "memberDelegatedProperty",
    "memberDelegatedExtensionProperty",
)
val EXPECTED_VALUES = listOf(
    TOPLEVEL_SUSPEND_FUNCTION,
    TOPLEVEL_FUNCTION,
    TOPLEVEL_EXTENSION_FUNCTION,
    TOPLEVEL_PROPERTY,
    TOPLEVEL_PROPERTY_NO_FIELD,
    TOPLEVEL_EXTENSION_PROPERTY,
    TOPLEVEL_DELEGATED_PROPERTY,
    TOPLEVEL_DELEGATED_EXTENSION_PROPERTY,
    MEMBER_SUSPEND_FUNCTION,
    MEMBER_FUNCTION,
    MEMBER_EXTENSION_FUNCTION,
    MEMBER_PROPERTY,
    MEMBER_PROPERTY_NO_FIELD,
    MEMBER_EXTENSION_PROPERTY,
    MEMBER_DELEGATED_PROPERTY,
    MEMBER_DELEGATED_EXTENSION_PROPERTY
)

@Enumerable
annotation class MyCustomAnnotation

@MyCustomAnnotation
suspend fun toplevelSuspendFunction() =
    TOPLEVEL_SUSPEND_FUNCTION

@MyCustomAnnotation
fun toplevelFunction() =
    TOPLEVEL_FUNCTION

@MyCustomAnnotation
fun Int.toplevelExtensionFunction() =
    TOPLEVEL_EXTENSION_FUNCTION

@MyCustomAnnotation
val toplevelProperty =
    TOPLEVEL_PROPERTY

@MyCustomAnnotation
val toplevelPropertyNoField: String
    get() = TOPLEVEL_PROPERTY_NO_FIELD

@MyCustomAnnotation
val Int.toplevelExtensionProperty: String
    get() = TOPLEVEL_EXTENSION_PROPERTY

@MyCustomAnnotation
val toplevelDelegatedProperty by lazy {
    TOPLEVEL_DELEGATED_PROPERTY
}

@MyCustomAnnotation
val Int.toplevelDelegatedExtensionProperty by lazy {
    TOPLEVEL_DELEGATED_EXTENSION_PROPERTY
}

class Foo {
    @MyCustomAnnotation
    suspend fun memberSuspendFunction() =
        MEMBER_SUSPEND_FUNCTION

    @MyCustomAnnotation
    fun memberFunction() =
        MEMBER_FUNCTION

    @MyCustomAnnotation
    fun Int.memberExtensionFunction() =
        MEMBER_EXTENSION_FUNCTION

    @MyCustomAnnotation
    val memberProperty =
        MEMBER_PROPERTY

    @MyCustomAnnotation
    val memberPropertyNoField: String
        get() = MEMBER_PROPERTY_NO_FIELD

    @MyCustomAnnotation
    val Int.memberExtensionProperty: String
        get() = MEMBER_EXTENSION_PROPERTY

    @MyCustomAnnotation
    val memberDelegatedProperty by lazy {
        MEMBER_DELEGATED_PROPERTY
    }

    @MyCustomAnnotation
    val Int.memberDelegatedExtensionProperty by lazy {
        MEMBER_DELEGATED_EXTENSION_PROPERTY
    }
}

fun main() = runBlocking {
    val elements = elementsWith<MyCustomAnnotation>()
        .keys.filterIsInstance<KCallable<String>>()

    EXPECTED_NAMES.forEachIndexed { i, name ->
        print(i.toString().padStart(2, '0'))

        // name matching is enough;
        //  why? issues with kotlin toplevel reflection instances
        //  and toplevel reflection instances implemented by ranno

        when {
            elements.any { e -> e.name == name } ->
                println(" \u001B[32mFOUND\u001B[0m $name")

            else ->
                println(" \u001B[31mMISSING\u001B[0m $name")
        }
    }

    val thisRef = Foo()
    val outputs = elements.map { element ->
        when (element.name) {
            "toplevelSuspendFunction" -> element.callSuspend()
            "toplevelFunction" -> element.call()
            "toplevelExtensionFunction" -> element.call(0)
            "toplevelProperty" -> element.call()
            "toplevelPropertyNoField" -> element.call()
            "toplevelExtensionProperty" -> element.call(0)
            "toplevelDelegatedProperty" -> element.call()
            "toplevelDelegatedExtensionProperty" -> element.call(0)
            "memberSuspendFunction" -> element.callSuspend(thisRef)
            "memberFunction" -> element.call(thisRef)
            "memberExtensionFunction" -> element.call(thisRef, 0)
            "memberProperty" -> element.call(thisRef)
            "memberPropertyNoField" -> element.call(thisRef)
            "memberExtensionProperty" -> element.call(thisRef, 0)
            "memberDelegatedProperty" -> element.call(thisRef)
            "memberDelegatedExtensionProperty" -> element.call(thisRef, 0)
            else -> error("Not supported: ${element.name}")
        }
    }

    println()

    EXPECTED_VALUES.forEachIndexed { i, it ->
        print(i.toString().padStart(2, '0'))

        when (it) {
            in outputs ->
                println(" \u001B[32mFOUND\u001B[0m $it")

            else ->
                println(" \u001B[31mMISSING\u001B[0m $it")
        }
    }
}
