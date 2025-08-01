package org.cufy.ranno.example.context_parameters

import org.cufy.ranno.toLambdaSuspend
import org.cufy.ranno.tryCast
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.kotlinFunction

class Something {
    context(a: Int, b: Double)
    fun Float.doSomething0(c: Byte) {
        println("Something0")
    }
}

context(_: Int, _: Float, _: Byte)
suspend fun doSomething1(a: String, b: Double) {
    error("Something1")
}

suspend fun main() {
    val function0 = Something::class.declaredFunctions.single()
    val lambda0 = function0.tryCast<Something, Int, Double, Float, Byte, Unit>()
        ?: error("Function cast failed: $function0")

    lambda0(Something(), 0, 0.0, 0f, 0b0)

    val klass1 = Class.forName("org.cufy.ranno.example.context_parameters.Main2Kt")
    val function1 = klass1.declaredMethods.single { it.name == "doSomething1" }.kotlinFunction!!
    val lambda1 = function1.toLambdaSuspend<Int, Float, Byte, String, Double, Unit>()
        ?: error("Function cast failed: $function1")

    lambda1(0, 0f, 0.toByte(), "", 0.0)
}
