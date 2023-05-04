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
package org.cufy.ranno

import org.cufy.ranno.internal.canApply
import org.cufy.ranno.internal.enumerateElementsWith
import org.intellij.lang.annotations.Language
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotations

//////////////////////////////////////////////////

/**
 * Marks the annotated annotation to be
 * processed by the ranno annotation processor.
 *
 * @author LSafer
 * @since 1.0.0
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class Enumerable

/**
 * An [@Enumerable][Enumerable] function/property
 * validator that ensures the annotated
 * function/property has a specific count,
 * position and types of parameters.
 *
 * @author LSafer
 * @since 1.0.0
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class EnumerableParameters(
    /**
     * This value defines the parameters count,
     * positions and types of valid functions or
     * property.
     *
     * If a function or a property is a class
     * member, its `this` argument is considered
     * its first parameter.
     *
     * If a function or property is an extension,
     * its `this` argument is considered its
     * first parameter unless it is a class member
     * then it will be the second parameter.
     *
     * @since 1.0.0
     */
    vararg val value: KClass<*>
)

/**
 * An [@Enumerable][Enumerable] function/property
 * validator that ensures the annotated
 * function/property has a specific return type.
 *
 * @author LSafer
 * @since 1.0.0
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class EnumerableReturnType(
    /**
     * This value defines the allowed return types
     * of a valid function or property.
     *
     * A valid function or property might have
     * exactly this class or a class that extends
     * this class.
     *
     * @since 1.0.0
     */
    val value: KClass<*>
)

/**
 * An [@Enumerable][Enumerable] class
 * validator that ensures the annotated
 * class extends a specific type.
 *
 * @author LSafer
 * @since 1.0.0
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class EnumerableSuperType(
    /**
     * This value defines the type a valid class
     * should extend.
     *
     * A valid class might extend exactly this
     * class or a class that extends this class.
     *
     * @since 1.0.0
     */
    val value: KClass<*>
)

//////////////////////////////////////////////////

/**
 * Return all the elements annotated with this annotation.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
val <T : Annotation> KClass<T>.annotatedElements: List<KAnnotatedElement>
    get() = elementsWith(this)

/**
 * Return all the functions annotated with this annotation.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
val <T : Annotation> KClass<T>.annotatedFunctions: List<KFunction<*>>
    get() = functionsWith(this)

/**
 * Return all the properties annotated with this annotation.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
val <T : Annotation> KClass<T>.annotatedProperties: List<KProperty<*>>
    get() = propertiesWith(this)

/**
 * Return all the classes annotated with this annotation.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
val <T : Annotation> KClass<T>.annotatedClasses: List<KClass<*>>
    get() = classesWith(this)

//////////////////////////////////////////////////

/**
 * Return all the elements annotated with [annotation] qualified name.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun elementsWith(annotation: String): List<KAnnotatedElement> {
    return enumerateElementsWith(annotation)
}

/**
 * Return all the elements annotated with [T].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun <T : Annotation> elementsWith(annotation: KClass<T>): List<KAnnotatedElement> {
    return enumerateElementsWith(annotation)
}

/**
 * Return all the elements annotated with [T].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
inline fun <reified T : Annotation> elementsWith(): List<KAnnotatedElement> {
    return elementsWith(T::class)
}

//////////////////////////////////////////////////

/**
 * Return all the functions annotated with [T].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun <T : Annotation> functionsWith(annotation: KClass<T>): List<KFunction<*>> {
    return elementsWith(annotation).filterIsInstance<KFunction<*>>()
}

/**
 * Return all the functions annotated with [T].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
inline fun <reified T : Annotation> functionsWith(): List<KFunction<*>> {
    return functionsWith(T::class)
}

//////////////////////////////////////////////////

/**
 * Return all the properties annotated with [T].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun <T : Annotation> propertiesWith(annotation: KClass<T>): List<KProperty<*>> {
    return elementsWith(annotation).filterIsInstance<KProperty<*>>()
}

/**
 * Return all the properties annotated with [T].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
inline fun <reified T : Annotation> propertiesWith(): List<KProperty<*>> {
    return propertiesWith(T::class)
}

//////////////////////////////////////////////////

/**
 * Return all the classes annotated with [T].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun <T : Annotation> classesWith(annotation: KClass<T>): List<KClass<*>> {
    return elementsWith(annotation).filterIsInstance<KClass<*>>()
}

/**
 * Return all the classes annotated with [T].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
inline fun <reified T : Annotation> classesWith(): List<KClass<*>> {
    return classesWith(T::class)
}

//////////////////////////////////////////////////

/**
 * Call the functions and properties annotated with [T]
 * with the given [arguments].
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun <T : Annotation> runWith(annotation: KClass<T>, vararg arguments: Any?): List<Any?> {
    return elementsWith(annotation)
        .asSequence()
        .filterIsInstance<KCallable<*>>()
        .filter { it.canApply(*arguments) }
        .map { it.call(*arguments) }
        .toList()
}

/**
 * Call the functions and properties annotated with [T]
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun <T : Annotation> runWith(annotation: KClass<T>, vararg arguments: Any?, predicate: (KCallable<*>) -> Boolean): List<Any?> {
    return elementsWith(annotation)
        .asSequence()
        .filterIsInstance<KCallable<*>>()
        .filter { it.canApply(*arguments) && predicate(it) }
        .map { it.call(*arguments) }
        .toList()
}

/**
 * Call the functions and properties annotated with [T]
 * with the given [arguments].
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
inline fun <reified T : Annotation> runWith(vararg arguments: Any?): List<Any?> {
    return runWith(T::class, *arguments)
}

/**
 * Call the functions and properties annotated with [T]
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
inline fun <reified T : Annotation> runWith(vararg arguments: Any?, noinline predicate: (KCallable<*>) -> Boolean): List<Any?> {
    return runWith(T::class, *arguments, predicate = predicate)
}

//////////////////////////////////////////////////

/**
 * Call the functions and properties annotated with [T]
 * with [this] as the argument.
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
fun <T : Annotation> Any.applyWith(annotation: KClass<T>, vararg arguments: Any?): List<Any?> {
    return runWith(annotation, this, *arguments)
}

/**
 * Call the functions and properties annotated with [T]
 * and matches the given [predicate]
 * with [this] as the argument.
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
fun <T : Annotation> Any.applyWith(annotation: KClass<T>, vararg arguments: Any?, predicate: (KCallable<*>) -> Boolean): List<Any?> {
    return runWith(annotation, this, *arguments, predicate = predicate)
}

/**
 * Call all the functions and properties annotated with [T]
 * with [this] as the argument.
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
inline fun <reified T : Annotation> Any.applyWith(vararg arguments: Any?): List<Any?> {
    return applyWith(T::class, *arguments)
}

/**
 * Call all the functions and properties annotated with [T]
 * and matches the given [predicate]
 * with [this] as the argument.
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
inline fun <reified T : Annotation> Any.applyWith(vararg arguments: Any?, noinline predicate: (KCallable<*>) -> Boolean): List<Any?> {
    return applyWith(T::class, *arguments, predicate = predicate)
}

//////////////////////////////////////////////////

/**
 * The default enumeration annotation.
 *
 * For structures without custom annotations.
 *
 * @author LSafer
 * @since 1.0.0
 */
@Enumerable
@Repeatable
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class Enumerated(
    /**
     * The enumeration qualifier.
     *
     * @since 1.0.0
     */
    val name: String = "",
    /**
     * Used to reduce conflict between multiple modules.
     *
     * @since 1.0.0
     */
    val domain: String = ""
)

/**
 * Call all the functions and properties annotated with [Enumerated]
 * with the given [arguments].
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun runEnumerated(vararg arguments: Any?): List<Any?> {
    return runWith<Enumerated>(*arguments)
}

/**
 * Call all the functions and properties annotated with [Enumerated]
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun runEnumerated(vararg arguments: Any?, predicate: (KCallable<*>) -> Boolean): List<Any?> {
    return runWith<Enumerated>(*arguments, predicate = predicate)
}

/**
 * Call all the functions and properties annotated with [Enumerated]
 * and matches the given [name] regexp and [domain] regexp
 * with the given [arguments].
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun runEnumerated(
    vararg arguments: Any?,
    @Language("RegExp") name: String = ".*",
    @Language("RegExp") domain: String = ".*"
) {
    val n = name.toRegex()
    val d = domain.toRegex()
    runEnumerated(*arguments) {
        it.findAnnotations<Enumerated>()
            .any { it.name matches n && it.domain matches d }
    }
}

/**
 * Call all the functions and properties annotated with [Enumerated]
 * with [this] as the argument.
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
fun Any.applyEnumerated(vararg arguments: Any?): List<Any?> {
    return applyWith<Enumerated>(*arguments)
}

/**
 * Call all the functions and properties annotated with [Enumerated]
 * and matches the given [predicate]
 * with [this] as the argument.
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
fun Any.applyEnumerated(vararg arguments: Any?, predicate: (KCallable<*>) -> Boolean): List<Any?> {
    return applyWith<Enumerated>(*arguments, predicate = predicate)
}

/**
 * Call all the functions and properties annotated with [Enumerated]
 * and matches the given [name] regexp and [domain] regexp
 * with [this] as the argument.
 *
 * Only functions and properties that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
fun Any.applyEnumerated(
    vararg arguments: Any?,
    @Language("RegExp") name: String = ".*",
    @Language("RegExp") domain: String = ".*"
): List<Any?> {
    val n = name.toRegex()
    val d = domain.toRegex()
    return applyWith<Enumerated>(*arguments) {
        it.findAnnotations<Enumerated>()
            .any { it.name matches n && it.domain matches d }
    }
}

//////////////////////////////////////////////////
