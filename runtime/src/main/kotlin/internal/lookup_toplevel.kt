/*
 *	Copyright 2023 cufy.org
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package org.cufy.ranno.internal

import java.lang.reflect.Method
import java.lang.reflect.Modifier.isStatic
import kotlin.reflect.*
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.jvm.kotlinProperty

/**
 * Use java reflection to obtain a [KFunction] instance
 * of the function in the given [klass] that has the
 * given [name] and [parameters].
 *
 * This is a workaround for kotlin toplevel reflection.
 *
 * @return the found function. Or `null` if not found.
 */
internal fun lookupToplevelFunction(klass: KClass<*>, name: String, parameters: List<KClass<*>>): KFunction<*>? {
    return klass.jvmMethods.firstOrNull {
        it.name == name &&
                it.parameters.map { it.type.kotlin } == parameters
    }?.kotlinFunction
}

/**
 * Use java reflection to obtain a [KProperty] instance
 * of the function in the given [klass] that has the
 * given [name] and [parameters].
 *
 * This is a workaround for kotlin toplevel reflection.
 *
 * This solution only works for toplevel properties with backing fields.
 *
 * @return the found property. Or `null` if not found.
 */
internal fun lookupToplevelProperty(klass: KClass<*>, name: String, parameters: List<KClass<*>>): KProperty<*>? {
    if (parameters.isNotEmpty()) return null

    return klass.jvmFields.firstOrNull {
        it.name == name && isStatic(it.modifiers)
    }?.kotlinProperty
}

/**
 * Use java reflection to obtain a [KProperty] instance
 * of the function in the given [klass] that has the
 * given [name] and [parameters].
 *
 * This is a workaround for kotlin toplevel reflection.
 *
 * This solution is for toplevel properties with no backing fields.
 *
 * This function will return a fake KProperty instance.
 *
 * @return the found property. Or `null` if not found.
 */
internal fun lookupToplevelPropertyNoBackingField(klass: KClass<*>, name: String, parameters: List<KClass<*>>): KProperty<*>? {
    val getterName = "get${name.replaceFirstChar { it.uppercase() }}"
    val setterName = "set${name.replaceFirstChar { it.uppercase() }}"
    val annotationsName = "$getterName\$annotations"
    val delegateName = "${name.replaceFirstChar { it.uppercase() }}\$delegate"

    val methods = klass.jvmMethods

    // find the getter method; if not found, the property does not exist
    val getter = methods.firstOrNull {
        it.name == getterName &&
                it.parameters.map { it.type.kotlin } == parameters
    }?.asToplevelKFunction<Any?>() ?: return null

    // find the setter method; if not found, the property is not mutable
    val setter = methods.firstOrNull {
        it.name == setterName &&
                it.parameters.dropLast(1).map { it.type.kotlin } == parameters
    }?.asToplevelKFunction<Unit>()

    // the annotations of the field are put on a method with a special name
    val annotations = methods.firstOrNull {
        it.name == annotationsName &&
                it.parameters.map { it.type.kotlin } == parameters
    }?.annotations.orEmpty().toList()

    val fields = klass.jvmFields

    // the delegate of the field is assigned to a field with a special name
    val delegate = lazy {
        fields.firstOrNull { it.name == delegateName && isStatic(it.modifiers) }
            ?.takeIf { it.trySetAccessibleAlternative() }
            ?.get(null)
    }

    return when (parameters.size) {
        0 -> when (setter) {
            null -> ToplevelKProperty0(name, annotations, getter, delegate)
            else -> ToplevelKMutableProperty0(name, annotations, getter, setter, delegate)
        }
        1 -> when (setter) {
            null -> ToplevelKProperty1(name, annotations, getter, delegate)
            else -> ToplevelKMutableProperty1(name, annotations, getter, setter, delegate)
        }
        else -> null
    }
}

/*
The following is fake implementations for KProperty
to be used for toplevel properties with no backing fields

toplevel properties are always final, static and, when it has a receiver, it is an extension receiver

since toplevel properties cannot have multiple receivers, KProperty2 and KMutableProperty2 was not implemented
*/

/** throw an [UnsupportedOperationException] with explanation of unavailability of a real toplevel reflection */
private fun unsupported(): Nothing {
    throw UnsupportedOperationException(
        "Full reflection is not supported for top-level properties with no backing fields."
    )
}

private fun <T> Method.asToplevelKFunction() = ToplevelKFunction<T>(this)

/** a custom implementation of [KFunction] for toplevel property accessor */
private class ToplevelKFunction<T>(private val method: Method) : KFunction<T> {
    override val isSuspend get() = false
    override val isFinal get() = true
    override val isOpen get() = false
    override val isAbstract get() = false
    override val isExternal get() = false
    override val isInfix get() = false
    override val isInline get() = false
    override val isOperator get() = false
    override val name: String get() = method.name

    override val visibility get() = unsupported()

    override val annotations by lazy {
        method.annotations.toList()
    }

    override val parameters: List<KParameter> by lazy {
        method.parameters.mapIndexed { index, parameter ->
            object : KParameter {
                override val index = index
                override val isOptional get() = false
                override val isVararg get() = false
                override val name: String? get() = parameter.name

                override val kind get() = unsupported()

                override val annotations by lazy { parameter.annotations.toList() }
                override val type: KType by lazy { parameter.type.kotlin.starProjectedType }
            }
        }
    }

    override val returnType: KType by lazy {
        method.returnType.kotlin.starProjectedType
    }

    override val typeParameters: List<KTypeParameter> by lazy {
        method.typeParameters.map { parameter ->
            object : KTypeParameter {
                override val name = parameter.name
                override val isReified = false

                override val upperBounds get() = unsupported()
                override val variance get() = unsupported()
            }
        }
    }

    override fun call(vararg args: Any?): T {
        @Suppress("UNCHECKED_CAST")
        return when {
            isStatic(method.modifiers) -> method.invoke(null, *args) as T
            else -> method.invoke(args.getOrNull(0), *args.drop(1).toTypedArray<Any?>()) as T
        }
    }

    override fun callBy(args: Map<KParameter, Any?>): T = unsupported()

    override fun toString(): String {
        return "fun $name(${parameters.joinToString(", ") { it.type.toString() }}): $returnType"
    }
}

/** a custom implementation of [KProperty0] for toplevel property */
private open class ToplevelKProperty0<V>(
    override val name: String,
    override val annotations: List<Annotation>,
    private val get: KFunction<V>,
    private val delegate: Lazy<Any?>
) : KProperty0<V>, KCallable<V> by get {
    override val isSuspend get() = false
    override val isFinal get() = true
    override val isOpen get() = false
    override val isAbstract get() = false
    override val isLateinit get() = false
    override val isConst get() = false

    override val visibility get() = unsupported()

    override val getter: KProperty0.Getter<V> by lazy {
        object : KProperty0.Getter<V>, KFunction<V> by get {
            override val property: KProperty<V> get() = this@ToplevelKProperty0

            override fun invoke(): V = get.call()
        }
    }

    override fun invoke(): V = get.call()
    override fun get(): V = get.call()
    override fun getDelegate(): Any? = delegate.value

    override fun toString(): String {
        return "val $name: $returnType"
    }
}

/** a custom implementation of [KMutableProperty0] for toplevel property */
private open class ToplevelKMutableProperty0<V>(
    name: String,
    annotations: List<Annotation>,
    get: KFunction<V>,
    private val set: KFunction<Unit>,
    delegate: Lazy<Any?>
) : ToplevelKProperty0<V>(name, annotations, get, delegate), KMutableProperty0<V> {
    override val setter: KMutableProperty0.Setter<V> by lazy {
        object : KMutableProperty0.Setter<V>, KFunction<Unit> by set {
            override val property: KProperty<V> get() = this@ToplevelKMutableProperty0

            override fun invoke(value: V) = set.call(value)
        }
    }

    override fun set(value: V) = set.call(value)

    override fun toString(): String {
        return "var $name: $returnType"
    }
}

/** a custom implementation of [KProperty1] for toplevel property */
private open class ToplevelKProperty1<out V>(
    override val name: String,
    override val annotations: List<Annotation>,
    private val get: KFunction<V>,
    private val delegate: Lazy<Any?>
) : KProperty1<Any?, V>, KCallable<V> by get {
    override val isSuspend get() = false
    override val isFinal get() = true
    override val isOpen get() = false
    override val isAbstract get() = false
    override val isLateinit get() = false
    override val isConst get() = false

    override val visibility get() = unsupported()

    override val getter: KProperty1.Getter<Any?, V> by lazy {
        object : KProperty1.Getter<Any?, V>, KFunction<V> by get {
            override val property: KProperty<V> get() = this@ToplevelKProperty1

            override fun invoke(receiver: Any?): V = get.call(receiver)
        }
    }

    override fun invoke(receiver: Any?): V = get.call(receiver)
    override fun get(receiver: Any?): V = get.call(receiver)
    override fun getDelegate(receiver: Any?): Any? = delegate.value

    override fun toString(): String {
        return "val ${parameters.single().type}.$name: $returnType"
    }
}

/** a custom implementation of [KMutableProperty1] for toplevel property */
private open class ToplevelKMutableProperty1<V>(
    name: String,
    annotations: List<Annotation>,
    get: KFunction<V>,
    private val set: KFunction<Unit>,
    delegate: Lazy<Any?>
) : ToplevelKProperty1<V>(name, annotations, get, delegate), KCallable<V>, KMutableProperty1<Any?, V> {
    override val setter: KMutableProperty1.Setter<Any?, V> by lazy {
        object : KMutableProperty1.Setter<Any?, V>, KFunction<Unit> by set {
            override val property: KProperty<V> get() = this@ToplevelKMutableProperty1

            override fun invoke(receiver: Any?, value: V) = set.call(receiver, value)
        }
    }

    override fun set(receiver: Any?, value: V) = set.call(receiver, value)

    override fun toString(): String {
        return "var ${parameters.single().type}.$name: $returnType"
    }
}
