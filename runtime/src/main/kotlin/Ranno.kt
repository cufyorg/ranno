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
 * Return all the elements annotated with [annotation].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun elementsWith(annotation: KClass<out Annotation>): List<KAnnotatedElement> {
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
inline fun <reified T : Annotation> elementsWith(predicate: (T) -> Boolean = { true }): List<KAnnotatedElement> {
    return elementsWith(T::class).filter { it.findAnnotations<T>().any(predicate) }
}

/**
 * Return all the elements annotated with [annotation].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun elementsWith(annotation: Annotation): List<KAnnotatedElement> {
    return elementsWith(annotation::class).filter { it.annotations.contains(annotation) }
}

//////////////////////////////////////////////////

/**
 * Return all the functions annotated with [annotation] qualified name.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun functionsWith(annotation: String): List<KFunction<*>> {
    return elementsWith(annotation).filterIsInstance<KFunction<*>>()
}

/**
 * Return all the functions annotated with [annotation].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun functionsWith(annotation: KClass<out Annotation>): List<KFunction<*>> {
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
inline fun <reified T : Annotation> functionsWith(predicate: (T) -> Boolean = { true }): List<KFunction<*>> {
    return functionsWith(T::class).filter { it.findAnnotations<T>().any(predicate) }
}

/**
 * Return all the functions annotated with [annotation].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun functionsWith(annotation: Annotation): List<KFunction<*>> {
    return elementsWith(annotation).filterIsInstance<KFunction<*>>()
}

//////////////////////////////////////////////////

/**
 * Return all the properties annotated with [annotation] qualified name.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun propertiesWith(annotation: String): List<KProperty<*>> {
    return elementsWith(annotation).filterIsInstance<KProperty<*>>()
}

/**
 * Return all the properties annotated with [annotation].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun propertiesWith(annotation: KClass<out Annotation>): List<KProperty<*>> {
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
inline fun <reified T : Annotation> propertiesWith(predicate: (T) -> Boolean = { true }): List<KProperty<*>> {
    return propertiesWith(T::class).filter { it.findAnnotations<T>().any(predicate) }
}

/**
 * Return all the properties annotated with [annotation].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun propertiesWith(annotation: Annotation): List<KProperty<*>> {
    return elementsWith(annotation).filterIsInstance<KProperty<*>>()
}

//////////////////////////////////////////////////

/**
 * Return all the classes annotated with [annotation] qualified name.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun classesWith(annotation: String): List<KClass<*>> {
    return elementsWith(annotation).filterIsInstance<KClass<*>>()
}

/**
 * Return all the classes annotated with [annotation].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun classesWith(annotation: KClass<out Annotation>): List<KClass<*>> {
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
inline fun <reified T : Annotation> classesWith(predicate: (T) -> Boolean = { true }): List<KClass<*>> {
    return classesWith(T::class).filter { it.findAnnotations<T>().any(predicate) }
}

/**
 * Return all the classes annotated with [annotation].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun classesWith(annotation: Annotation): List<KClass<*>> {
    return elementsWith(annotation).filterIsInstance<KClass<*>>()
}

//////////////////////////////////////////////////

/**
 * Call the functions and properties annotated with [annotation] qualified name
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
fun runWith(annotation: String, vararg arguments: Any?, predicate: (KCallable<*>) -> Boolean = { true }): List<Any?> {
    return elementsWith(annotation).mapMaybeInvoke(arguments, predicate)
}

/**
 * Call the functions and properties annotated with [annotation]
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
fun runWith(annotation: KClass<out Annotation>, vararg arguments: Any?, predicate: (KCallable<*>) -> Boolean = { true }): List<Any?> {
    return elementsWith(annotation).mapMaybeInvoke(arguments, predicate)
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
inline fun <reified T : Annotation> runWith(vararg arguments: Any?, noinline predicate: (T) -> Boolean = { true }): List<Any?> {
    return runWith(T::class, *arguments) { it.findAnnotations<T>().any(predicate) }
}

/**
 * Call the functions and properties annotated with [annotation]
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
fun runWith(annotation: Annotation, vararg arguments: Any?): List<Any?> {
    return elementsWith(annotation).mapMaybeInvoke(arguments)
}

//////////////////////////////////////////////////

/**
 * Call the functions and properties annotated with [annotation]
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
fun Any.applyWith(annotation: String, vararg arguments: Any?, predicate: (KCallable<*>) -> Boolean = { true }): List<Any?> {
    return runWith(annotation, this, *arguments, predicate = predicate)
}

/**
 * Call the functions and properties annotated with [annotation]
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
fun Any.applyWith(annotation: KClass<out Annotation>, vararg arguments: Any?, predicate: (KCallable<*>) -> Boolean = { true }): List<Any?> {
    return runWith(annotation, this, *arguments, predicate = predicate)
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
inline fun <reified T : Annotation> Any.applyWith(vararg arguments: Any?, noinline predicate: (T) -> Boolean = { true }): List<Any?> {
    return applyWith(T::class, *arguments) { it.findAnnotations<T>().any(predicate) }
}

/**
 * Call the functions and properties annotated with [annotation]
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
fun Any.applyWith(annotation: Annotation, vararg arguments: Any?): List<Any?> {
    return runWith(annotation, this, *arguments)
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

//////////////////////////////////////////////////

private inline fun Iterable<KAnnotatedElement>.mapMaybeInvoke(
    arguments: Array<out Any?>,
    crossinline predicate: (KCallable<*>) -> Boolean = { true }
): List<Any?> {
    return asSequence()
        .filterIsInstance<KCallable<*>>()
        .filter { it.canApply(*arguments) && predicate(it) }
        .map { it.call(*arguments) }
        .toList()
}
