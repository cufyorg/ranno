@file:JvmName("RannoKt")
@file:JvmMultifileClass

/*
 *	Copyright 2023-2024 cufy.org
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
import org.cufy.ranno.internal.qualifiedNameOrThrow
import org.cufy.ranno.internal.requireEnumerable
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotations

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
    requireEnumerable(annotation)
    return elementsWith(annotation.qualifiedNameOrThrow)
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
    return elementsWith(annotation::class)
        .filter { annotation in it.annotations }
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
    return elementsWith(annotation)
        .filterIsInstance<KFunction<*>>()
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
    return elementsWith(annotation)
        .filterIsInstance<KFunction<*>>()
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
    return elementsWith(annotation)
        .filterIsInstance<KFunction<*>>()
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
    return elementsWith(annotation)
        .filterIsInstance<KProperty<*>>()
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
    return elementsWith(annotation)
        .filterIsInstance<KProperty<*>>()
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
    return elementsWith(annotation)
        .filterIsInstance<KProperty<*>>()
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
    return elementsWith(annotation)
        .filterIsInstance<KClass<*>>()
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
    return elementsWith(annotation)
        .filterIsInstance<KClass<*>>()
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
    return elementsWith(annotation)
        .filterIsInstance<KClass<*>>()
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

//////////////////////////////////////////////////
