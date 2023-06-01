package org.cufy.ranno.example

import kotlinx.coroutines.runBlocking
import org.cufy.ranno.*
import kotlin.test.assertEquals
import kotlin.test.fail

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

val EXPECTED = listOf(
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
    val out = runWithSuspend<MyCustomAnnotation>(Foo(), 0) +
            runWithSuspend<MyCustomAnnotation>(0)

    val missing = EXPECTED subtract out.toSet()

    if (missing.isNotEmpty()) {
        fail("Component was not run: ${missing.joinToString(", ") { "'$it'" }}")
    }

    assertEquals(EXPECTED.size + 5, out.size) // +5 for the argument-less properties and functions
}
