package org.cufy.ranno.example.context_parameters

import kotlinx.coroutines.runBlocking
import org.cufy.ranno.Enumerable
import org.cufy.ranno.elementsWith
import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.full.callSuspend

const val TOPLEVEL_SUSPEND_FUNCTION_CTX = "Toplevel Suspend Function CTX"

const val TOPLEVEL_FUNCTION_CTX = "Toplevel Function CTX"
const val TOPLEVEL_EXTENSION_FUNCTION_CTX = "Toplevel Extension Function CTX"

const val TOPLEVEL_PROPERTY_NO_FIELD_CTX = "Toplevel Property (No Field) CTX"
const val TOPLEVEL_EXTENSION_PROPERTY_CTX = "Toplevel Extension Property CTX"

const val MEMBER_SUSPEND_FUNCTION_CTX = "Member Suspend Function CTX"

const val MEMBER_FUNCTION_CTX = "Member Function CTX"
const val MEMBER_EXTENSION_FUNCTION_CTX = "Member Extension Function CTX"

const val MEMBER_PROPERTY_NO_FIELD_CTX = "Member Property (No Field) CTX"
const val MEMBER_EXTENSION_PROPERTY_CTX = "Member Extension Property CTX"

val EXPECTED_NAMES = listOf(
    "toplevelSuspendFunctionCtx",
    "toplevelFunctionCtx",
    "toplevelExtensionFunctionCtx",
    "toplevelPropertyNoFieldCtx",
    "toplevelExtensionPropertyCtx",
    "memberSuspendFunctionCtx",
    "memberFunctionCtx",
    "memberExtensionFunctionCtx",
    "memberPropertyNoFieldCtx",
    "memberExtensionPropertyCtx",
)
val EXPECTED_VALUES = listOf(
    TOPLEVEL_SUSPEND_FUNCTION_CTX,
    TOPLEVEL_FUNCTION_CTX,
    TOPLEVEL_EXTENSION_FUNCTION_CTX,
    TOPLEVEL_PROPERTY_NO_FIELD_CTX,
    TOPLEVEL_EXTENSION_PROPERTY_CTX,
    MEMBER_SUSPEND_FUNCTION_CTX,
    MEMBER_FUNCTION_CTX,
    MEMBER_EXTENSION_FUNCTION_CTX,
    MEMBER_PROPERTY_NO_FIELD_CTX,
    MEMBER_EXTENSION_PROPERTY_CTX,
)

@Enumerable
annotation class MyCustomAnnotation

@MyCustomAnnotation
context(_: Locale, _: UUID)
suspend fun toplevelSuspendFunctionCtx() =
    TOPLEVEL_SUSPEND_FUNCTION_CTX

@MyCustomAnnotation
context(_: Locale, _: UUID)
fun toplevelFunctionCtx() =
    TOPLEVEL_FUNCTION_CTX

@MyCustomAnnotation
context(_: Locale, _: UUID)
fun Int.toplevelExtensionFunctionCtx() =
    TOPLEVEL_EXTENSION_FUNCTION_CTX

@MyCustomAnnotation
context(_: Locale, _: UUID)
val toplevelPropertyNoFieldCtx: String
    get() = TOPLEVEL_PROPERTY_NO_FIELD_CTX

@MyCustomAnnotation
context(_: Locale, _: UUID)
val Int.toplevelExtensionPropertyCtx: String
    get() = TOPLEVEL_EXTENSION_PROPERTY_CTX

class Foo {
    @MyCustomAnnotation
    context(_: Locale, _: UUID)
    suspend fun memberSuspendFunctionCtx() =
        MEMBER_SUSPEND_FUNCTION_CTX

    @MyCustomAnnotation
    context(_: Locale, _: UUID)
    fun memberFunctionCtx() =
        MEMBER_FUNCTION_CTX

    @MyCustomAnnotation
    context(_: Locale, _: UUID)
    fun Int.memberExtensionFunctionCtx() =
        MEMBER_EXTENSION_FUNCTION_CTX

    @MyCustomAnnotation
    context(_: Locale, _: UUID)
    val memberPropertyNoFieldCtx: String
        get() = MEMBER_PROPERTY_NO_FIELD_CTX

    @MyCustomAnnotation
    context(_: Locale, _: UUID)
    val Int.memberExtensionPropertyCtx: String
        get() = MEMBER_EXTENSION_PROPERTY_CTX
}

fun main() = runBlocking {
    val elements = elementsWith<MyCustomAnnotation>()
        .keys.filterIsInstance<KCallable<*>>()

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

    val ctx0 = Locale.getDefault()
    val ctx1 = UUID.randomUUID()
    val thisRef = Foo()
    val outputs = elements.map { element ->
        when (element.name) {
            "toplevelSuspendFunctionCtx" -> element.callSuspend(ctx0, ctx1)
            "toplevelFunctionCtx" -> element.call(ctx0, ctx1)
            "toplevelExtensionFunctionCtx" -> element.call(ctx0, ctx1, 0)
            "toplevelPropertyNoFieldCtx" -> element.call(ctx0, ctx1)
            "toplevelExtensionPropertyCtx" -> element.call(ctx0, ctx1, 0)
            "memberSuspendFunctionCtx" -> element.callSuspend(thisRef, ctx0, ctx1)
            "memberFunctionCtx" -> element.call(thisRef, ctx0, ctx1)
            "memberExtensionFunctionCtx" -> element.call(thisRef, ctx0, ctx1, 0)
            "memberPropertyNoFieldCtx" -> element.call(thisRef, ctx0, ctx1)
            "memberExtensionPropertyCtx" -> element.call(thisRef, ctx0, ctx1, 0)
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
