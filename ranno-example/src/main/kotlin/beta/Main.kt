package org.cufy.ranno.example.beta

import kotlinx.coroutines.runBlocking
import org.cufy.ranno.Enumerated
import org.cufy.ranno.valuesWith

const val TOPLEVEL_VALUE = "Toplevel Value"
const val TOPLEVEL_COMPUTED_VALUE = "Toplevel Computed Value"
const val TOPLEVEL_EXTENSION_VALUE = "Toplevel Extension Value"
const val TOPLEVEL_DELEGATED_VALUE = "Toplevel Delegated Value"
const val TOPLEVEL_DELEGATED_EXTENSION_VALUE = "Toplevel Delegated Extension Value"

const val MEMBER_VALUE = "Member Value"
const val MEMBER_COMPUTED_VALUE = "Member Computed Value"
const val MEMBER_EXTENSION_VALUE = "Member Extension Value"
const val MEMBER_DELEGATED_VALUE = "Member Delegated Value"
const val MEMBER_DELEGATED_EXTENSION_VALUE = "Member Delegated Extension Value"

val EXPECTED_VALUES = setOf(
    TOPLEVEL_VALUE,
    TOPLEVEL_COMPUTED_VALUE,
    TOPLEVEL_EXTENSION_VALUE,
    TOPLEVEL_DELEGATED_VALUE,
    TOPLEVEL_DELEGATED_EXTENSION_VALUE,

    MEMBER_VALUE,
    MEMBER_COMPUTED_VALUE,
    MEMBER_EXTENSION_VALUE,
    MEMBER_DELEGATED_VALUE,
    MEMBER_DELEGATED_EXTENSION_VALUE,
)

object Ext
object Ins {
    @Enumerated
    val memberValue =
        MEMBER_VALUE

    @Enumerated
    val memberComputedValue: String
        get() = MEMBER_COMPUTED_VALUE

    @Enumerated
    val Ext.memberExtensionValue: String
        get() = MEMBER_EXTENSION_VALUE

    @Enumerated
    val memberDelegatedValue by lazy {
        MEMBER_DELEGATED_VALUE
    }

    @Enumerated
    val Ext.memberDelegatedExtensionValue by lazy {
        MEMBER_DELEGATED_EXTENSION_VALUE
    }
}

@Enumerated
val toplevelValue =
    TOPLEVEL_VALUE

@Enumerated
val toplevelComputedValue: String
    get() = TOPLEVEL_COMPUTED_VALUE

@Enumerated
val Ext.toplevelExtensionValue: String
    get() = TOPLEVEL_EXTENSION_VALUE

@Enumerated
val toplevelDelegatedValue by lazy {
    TOPLEVEL_DELEGATED_VALUE
}

@Enumerated
val Ext.toplevelDelegatedExtensionValue by lazy {
    TOPLEVEL_DELEGATED_EXTENSION_VALUE
}

fun main() = runBlocking {
    val values = valuesWith<Enumerated>()

    EXPECTED_VALUES.forEachIndexed { i, it ->
        val n = i.toString().padStart(2, '0')

        when (it) {
            in values -> println("$n \u001B[32mFOUND\u001B[0m $it")
            else -> println("$n \u001B[31mMISSING\u001B[0m $it")
        }
    }
}
