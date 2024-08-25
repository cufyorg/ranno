package org.cufy.ranno.example.gamma

import kotlinx.coroutines.delay
import org.cufy.ranno.Enumerated
import org.cufy.ranno.functionsWith
import org.cufy.ranno.tryCast
import org.cufy.ranno.tryCastSuspend

const val TOPLEVEL_FUNCTION = "Toplevel Function"
const val TOPLEVEL_SUSPEND_FUNCTION = "Toplevel Suspend Function"

@Enumerated
fun toplevelFunction(): String {
    return TOPLEVEL_FUNCTION
}

@Enumerated
suspend fun toplevelSuspendFunction(): String {
    delay(0)
    return TOPLEVEL_SUSPEND_FUNCTION
}

val EXPECTED_RETURNS = listOf(
    TOPLEVEL_FUNCTION,
)

val EXPECTED_SUSPEND_RETURNS = listOf(
    TOPLEVEL_SUSPEND_FUNCTION,
)

suspend fun main() {
    val returns = functionsWith(Enumerated::class)
        .mapNotNull { it.tryCast<String>() }
        .map { it() }

    val suspendReturns = functionsWith(Enumerated::class)
        .mapNotNull { it.tryCastSuspend<String>() }
        .map { it() }

    EXPECTED_RETURNS.forEachIndexed { i, it ->
        val n = i.toString().padStart(2, '0')

        when (it) {
            in returns -> println("$n \u001B[32mFOUND\u001B[0m $it")
            else -> println("$n \u001B[31mMISSING\u001B[0m $it")
        }
    }
    EXPECTED_SUSPEND_RETURNS.forEachIndexed { i, it ->
        val n = i.plus(EXPECTED_RETURNS.size)
            .toString().padStart(2, '0')

        when (it) {
            in suspendReturns -> println("$n \u001B[32mFOUND\u001B[0m $it")
            else -> println("$n \u001B[31mMISSING\u001B[0m $it")
        }
    }
}
