package org.cufy.ranno.example.context_parameters

import org.cufy.ranno.tryCast
import kotlin.reflect.full.declaredFunctions

class Something {
    context(a: Int, b: Double)
    fun Float.doSomething0(c: Byte) {
        println("Something0")
    }
}

fun main() {
    val function = Something::class.declaredFunctions.single()
//    @Suppress("UNCHECKED_CAST")
//    val lambda = function as (Something, Int, Double, Float, Byte) -> Unit

    val lambda = function.tryCast<Something, Int, Double, Float, Byte, Unit>()
        ?: error("Function cast failed: $function")

    lambda(Something(), 0, 0.0, 0f, 0b0)
}
