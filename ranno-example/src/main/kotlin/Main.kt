package org.cufy.ranno.example

import kotlinx.coroutines.runBlocking
import org.cufy.ranno.Enumerable
import org.cufy.ranno.elementsWith
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty2
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberExtensionProperties
import kotlin.reflect.jvm.jvmErasure

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

@Suppress("UNCHECKED_CAST")
val Foo_Int_memberExtensionFunction = Foo::class.functions.first {
    it.name == "memberExtensionFunction"
} as KFunction<String>

@Suppress("UNCHECKED_CAST")
val Foo_Int_memberExtensionProperty = Foo::class.memberExtensionProperties.first {
    it.name == "memberExtensionProperty"
} as KProperty2<Foo, Int, String>

@Suppress("UNCHECKED_CAST")
val Foo_Int_memberDelegatedExtensionProperty = Foo::class.memberExtensionProperties.first {
    it.name == "memberDelegatedExtensionProperty"
} as KProperty2<Foo, Int, String>

val EXPECTED_TOPLEVEL_ELEMENTS: List<KCallable<String>> = listOf(
    ::toplevelSuspendFunction,
    ::toplevelFunction,
    Int::toplevelExtensionFunction,
    ::toplevelProperty,
    ::toplevelPropertyNoField,
    Int::toplevelExtensionProperty,
    ::toplevelDelegatedProperty,
    Int::toplevelDelegatedExtensionProperty,
    Foo::memberSuspendFunction,
    Foo::memberFunction,
    Foo_Int_memberExtensionFunction, // Foo::Int::memberExtensionFunction
    Foo::memberProperty,
    Foo::memberPropertyNoField,
    Foo_Int_memberExtensionProperty, // Foo::Int::memberExtensionProperty
    Foo::memberDelegatedProperty,
    Foo_Int_memberDelegatedExtensionProperty // Foo::Int::memberDelegatedExtensionProperty
)

fun main() = runBlocking {
    val elements = elementsWith<MyCustomAnnotation>()
        .keys.filterIsInstance<KCallable<*>>()

    EXPECTED_TOPLEVEL_ELEMENTS.forEachIndexed { i, it ->
        print(i.toString().padStart(2, '0'))

        // name matching is enough;
        //  why? issues with kotlin toplevel reflection instances
        //  and toplevel reflection instances implemented by ranno

        when {
            elements.any { e -> e.name == it.name } ->
                println(" \u001B[32mFOUND\u001B[0m $it")

            else ->
                println(" \u001B[31mMISSING\u001B[0m $it")
        }
    }

    val outputs = elements.mapNotNull { element ->
        when {
            element.parameters.isEmpty() ->
                element.callSuspend()

            element.parameters.size == 2 ->
                element.callSuspend(Foo(), 0)

            element.parameters[0].type.jvmErasure == Int::class ->
                element.callSuspend(0)

            element.parameters[0].type.jvmErasure == Foo::class ->
                element.callSuspend(Foo())

            else -> error("!")
        }
    }

    println()

    EXPECTED.forEachIndexed { i, it ->
        print(i.toString().padStart(2, '0'))

        when (it) {
            in outputs ->
                println(" \u001B[32mFOUND\u001B[0m $it")

            else ->
                println(" \u001B[31mMISSING\u001B[0m $it")
        }
    }
}
