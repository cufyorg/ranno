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

import org.cufy.ranno.internal.enumerateElementsWith
import org.cufy.ranno.internal.trySetAccessibleAlternative
import kotlin.reflect.*
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.jvm.jvmErasure

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
    vararg val value: KClass<*>,
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
    val value: KClass<*>,
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
    val value: KClass<*>,
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
inline fun <reified T : Annotation> elementsWith(predicate: (T) -> Boolean): List<KAnnotatedElement> {
    return elementsWith(T::class).filter { it.findAnnotations<T>().any(predicate) }
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
inline fun <reified T : Annotation> elementsWith(): Map<KAnnotatedElement, List<T>> {
    return elementsWith(T::class)
        .asSequence()
        .map { it to it.findAnnotations<T>() }
        .filter { (_, annotations) -> annotations.isNotEmpty() }
        .toMap()
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
    return elementsWith(annotation::class).filter { annotation in it.annotations }
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
inline fun <reified T : Annotation> functionsWith(predicate: (T) -> Boolean): List<KFunction<*>> {
    return functionsWith(T::class).filter { it.findAnnotations<T>().any(predicate) }
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
inline fun <reified T : Annotation> functionsWith(): Map<KFunction<*>, List<T>> {
    return functionsWith(T::class)
        .asSequence()
        .map { it to it.findAnnotations<T>() }
        .filter { (_, annotations) -> annotations.isNotEmpty() }
        .toMap()
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
inline fun <reified T : Annotation> propertiesWith(predicate: (T) -> Boolean): List<KProperty<*>> {
    return propertiesWith(T::class).filter { it.findAnnotations<T>().any(predicate) }
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
inline fun <reified T : Annotation> propertiesWith(): Map<KProperty<*>, List<T>> {
    return propertiesWith(T::class)
        .asSequence()
        .map { it to it.findAnnotations<T>() }
        .filter { (_, annotations) -> annotations.isNotEmpty() }
        .toMap()
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
inline fun <reified T : Annotation> classesWith(predicate: (T) -> Boolean): List<KClass<*>> {
    return classesWith(T::class).filter { it.findAnnotations<T>().any(predicate) }
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
inline fun <reified T : Annotation> classesWith(): Map<KClass<*>, List<T>> {
    return classesWith(T::class)
        .asSequence()
        .map { it to it.findAnnotations<T>() }
        .filter { (_, annotations) -> annotations.isNotEmpty() }
        .toMap()
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
 * Call the functions annotated with [annotation] qualified name
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * __Note: Suspend functions won't be run with this
 * function even if a continuation is passed as the
 * last argument. To also invoke suspending functions
 * use [runWithSuspend].__
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun runWith(annotation: String, vararg arguments: Any?, predicate: (KFunction<*>) -> Boolean = { true }): List<Any?> {
    @OptIn(ExperimentalRannoApi::class)
    return elementsWith(annotation)
        .asSequence()
        .filterIsInstance<KFunction<*>>()
        .filter { it.canCallWith(*arguments) && predicate(it) }
        .map { it.callWith(*arguments) }
        .toList()
}

/**
 * Call the functions annotated with [annotation]
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * __Note: Suspend functions won't be run with this
 * function even if a continuation is passed as the
 * last argument. To also invoke suspending functions
 * use [runWithSuspend].__
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun runWith(annotation: KClass<out Annotation>, vararg arguments: Any?, predicate: (KFunction<*>) -> Boolean = { true }): List<Any?> {
    @OptIn(ExperimentalRannoApi::class)
    return elementsWith(annotation)
        .asSequence()
        .filterIsInstance<KFunction<*>>()
        .filter { it.canCallWith(*arguments) && predicate(it) }
        .map { it.callWith(*arguments) }
        .toList()
}

/**
 * Call the functions annotated with [T]
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * __Note: Suspend functions won't be run with this
 * function even if a continuation is passed as the
 * last argument. To also invoke suspending functions
 * use [runWithSuspend].__
 *
 * Only functions that can be invoked with the
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
 * Call the functions annotated with [annotation]
 * with the given [arguments].
 *
 * __Note: Suspend functions won't be run with this
 * function even if a continuation is passed as the
 * last argument. To also invoke suspending functions
 * use [runWithSuspend].__
 *
 * Only functions that can be invoked with the
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
    return runWith(annotation::class, *arguments) { annotation in it.annotations }
}

//////////////////////////////////////////////////

/**
 * Call the functions annotated with [annotation] qualified name
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
suspend fun runWithSuspend(annotation: String, vararg arguments: Any?, predicate: (KFunction<*>) -> Boolean = { true }): List<Any?> {
    @OptIn(ExperimentalRannoApi::class)
    return elementsWith(annotation)
        .asSequence()
        .filterIsInstance<KFunction<*>>()
        .filter { it.canCallWithSuspend(*arguments) && predicate(it) }
        .mapTo(mutableListOf()) { it.callWithSuspend(*arguments) }
}

/**
 * Call the functions annotated with [annotation]
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
suspend fun runWithSuspend(annotation: KClass<out Annotation>, vararg arguments: Any?, predicate: (KFunction<*>) -> Boolean = { true }): List<Any?> {
    @OptIn(ExperimentalRannoApi::class)
    return elementsWith(annotation)
        .asSequence()
        .filterIsInstance<KFunction<*>>()
        .filter { it.canCallWithSuspend(*arguments) && predicate(it) }
        .mapTo(mutableListOf()) { it.callWithSuspend(*arguments) }
}

/**
 * Call the functions annotated with [T]
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
suspend inline fun <reified T : Annotation> runWithSuspend(vararg arguments: Any?, noinline predicate: (T) -> Boolean = { true }): List<Any?> {
    return runWithSuspend(T::class, *arguments) { it.findAnnotations<T>().any(predicate) }
}

/**
 * Call the functions annotated with [annotation]
 * with the given [arguments].
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
suspend fun runWithSuspend(annotation: Annotation, vararg arguments: Any?): List<Any?> {
    return runWithSuspend(annotation::class, *arguments) { annotation in it.annotations }
}

//////////////////////////////////////////////////

/**
 * Call the functions annotated with [annotation]
 * and matches the given [predicate]
 * with [this] as the argument.
 *
 * __Note: Suspend functions won't be run with this
 * function even if a continuation is passed as the
 * last argument. To also invoke suspending functions
 * use [applyWithSuspend].__
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
fun Any.applyWith(annotation: String, vararg arguments: Any?, predicate: (KFunction<*>) -> Boolean = { true }): List<Any?> {
    return runWith(annotation, this, *arguments, predicate = predicate)
}

/**
 * Call the functions annotated with [annotation]
 * and matches the given [predicate]
 * with [this] as the argument.
 *
 * __Note: Suspend functions won't be run with this
 * function even if a continuation is passed as the
 * last argument. To also invoke suspending functions
 * use [applyWithSuspend].__
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
fun Any.applyWith(annotation: KClass<out Annotation>, vararg arguments: Any?, predicate: (KFunction<*>) -> Boolean = { true }): List<Any?> {
    return runWith(annotation, this, *arguments, predicate = predicate)
}

/**
 * Call all the functions annotated with [T]
 * and matches the given [predicate]
 * with [this] as the argument.
 *
 * __Note: Suspend functions won't be run with this
 * function even if a continuation is passed as the
 * last argument. To also invoke suspending functions
 * use [applyWithSuspend].__
 *
 * Only functions that can be invoked with the
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
    return runWith<T>(this, *arguments, predicate = predicate)
}

/**
 * Call the functions annotated with [annotation]
 * with [this] as the argument.
 *
 * __Note: Suspend functions won't be run with this
 * function even if a continuation is passed as the
 * last argument. To also invoke suspending functions
 * use [applyWithSuspend].__
 *
 * Only functions that can be invoked with the
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
 * Call the functions annotated with [annotation]
 * and matches the given [predicate]
 * with [this] as the argument.
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
suspend fun Any.applyWithSuspend(annotation: String, vararg arguments: Any?, predicate: (KFunction<*>) -> Boolean = { true }): List<Any?> {
    return runWithSuspend(annotation, this, *arguments, predicate = predicate)
}

/**
 * Call the functions annotated with [annotation]
 * and matches the given [predicate]
 * with [this] as the argument.
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
suspend fun Any.applyWithSuspend(annotation: KClass<out Annotation>, vararg arguments: Any?, predicate: (KFunction<*>) -> Boolean = { true }): List<Any?> {
    return runWithSuspend(annotation, this, *arguments, predicate = predicate)
}

/**
 * Call all the functions annotated with [T]
 * and matches the given [predicate]
 * with [this] as the argument.
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
suspend inline fun <reified T : Annotation> Any.applyWithSuspend(vararg arguments: Any?, noinline predicate: (T) -> Boolean = { true }): List<Any?> {
    return runWithSuspend<T>(this, *arguments, predicate = predicate)
}

/**
 * Call the functions annotated with [annotation]
 * with [this] as the argument.
 *
 * Only functions that can be invoked with the
 * provided arguments will be invoked.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments additional arguments.
 * @since 1.0.0
 */
suspend fun Any.applyWithSuspend(annotation: Annotation, vararg arguments: Any?): List<Any?> {
    return runWithSuspend(annotation, this, *arguments)
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
    val domain: String = "",
)

/**
 * The default configuration enumeration annotation.
 *
 * For structures without custom annotations.
 *
 * Example (Ktor) :
 *
 * ```kt
 * fun Route.routes() {
 *      applyWith<EnumeratedScript>(3) {
 *          it.domain == "com.example"
 *      }
 * }
 *
 * @EnumeratedScript(domain= "com.example")
 * fun Route.__Routes() {
 *      get { /*...*/ }
 *      post { /*...*/ }
 * }
 *
 * @EnumeratedScript(domain= "com.example")
 * fun Route.__RoutesWithArgument(number: Int) {
 * }
 * ```
 *
 * @author LSafer
 * @since 1.0.0
 */
@Enumerable
@Repeatable
@EnumerableReturnType(Unit::class)
@Target(AnnotationTarget.FUNCTION)
annotation class EnumeratedScript(
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
    val domain: String = "",
)

/**
 * Adds compile time restriction to element usage
 * to be only used via enumeration functions and
 * not directly.
 *
 * @author LSafer
 * @since 1.0.0
 */
@RequiresOptIn("This component is intended to be used via enumeration and not directly.", RequiresOptIn.Level.ERROR)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class EnumerationOnly

//////////////////////////////////////////////////

/**
 * Return true if this callable can be call with the given [arguments].
 *
 * If the callable accepts fewer arguments,
 * the extra arguments are ignored.
 *
 * @since 1.0.0
 */
@ExperimentalRannoApi
fun KFunction<*>.canCallWith(vararg arguments: Any?): Boolean {
    return !isSuspend && canCallWithSuspend(*arguments)
}

/**
 * Call this callable with the given [arguments].
 *
 * If the callable accepts fewer arguments,
 * the extra arguments are ignored.
 *
 * @return the returned value.
 * @since 1.0.0
 */
@ExperimentalRannoApi
fun KFunction<*>.callWith(vararg arguments: Any?): Any? {
    trySetAccessibleAlternative()

    return if (parameters.size == arguments.size)
        call(*arguments)
    else
        call(*arguments.take(parameters.size).toTypedArray())
}

/**
 * Return true if this callable can be call with the given [arguments].
 *
 * If the callable accepts fewer arguments,
 * the extra arguments are ignored.
 *
 * @since 1.0.0
 */
@ExperimentalRannoApi
fun KFunction<*>.canCallWithSuspend(vararg arguments: Any?): Boolean {
    if (parameters.size > arguments.size)
        return false

    for (i in parameters.indices) {
        val parameter = parameters[i]
        val argument = arguments[i]

        if (!parameter.type.jvmErasure.isInstance(argument))
            return false
    }

    return true
}

/**
 * Call this callable with the given [arguments].
 *
 * If the callable accepts fewer arguments,
 * the extra arguments are ignored.
 *
 * @return the returned value.
 * @since 1.0.0
 */
@ExperimentalRannoApi
suspend fun KFunction<*>.callWithSuspend(vararg arguments: Any?): Any? {
    trySetAccessibleAlternative()

    return if (parameters.size == arguments.size)
        callSuspend(*arguments)
    else
        callSuspend(*arguments.take(parameters.size).toTypedArray())
}

/**
 * Return true if this function can be call with the given [parameters]
 * and returns instance of [returnType].
 *
 * @param suspend pass true to match suspend functions
 */
fun KFunction<*>.matchSignature(returnType: KType, vararg parameters: KType, suspend: Boolean = false): Boolean {
    return matchSignature(returnType, parameters.asList(), suspend)
}

/**
 * Return true if this function can be call with the given [parameters]
 * and returns instance of [returnType].
 *
 * @param suspend pass true to match suspend functions
 */
fun KFunction<*>.matchSignature(returnType: KType, parameters: List<KType>, suspend: Boolean = false): Boolean {
    if (!suspend && this.isSuspend)
        return false

    if (!this.returnType.isSubtypeOf(returnType))
        return false

    if (this.parameters.size != parameters.size)
        return false

    for (i in this.parameters.indices)
        if (!this.parameters[i].type.isSupertypeOf(parameters[i]))
            return false

    return true
}
