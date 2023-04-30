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
package org.cufy.ranno.ktor

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.cufy.ranno.*
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction

//////////////////////////////////////////////////

/**
 * The default ktor route enumeration annotation.
 *
 * For structures without custom annotations.
 *
 * @author LSafer
 * @since 1.0.0
 */
@Enumerable
@EnumerableParameters(Route::class)
@EnumerableReturnType(Unit::class)
@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class EnumeratedRoute(
    /**
     * The enumeration qualifier.
     *
     * @since 1.0.0
     */
    val value: String = "",
    /**
     * Used to reduce conflict between multiple modules.
     *
     * @since 1.0.0
     */
    val domain: String = ""
)

/**
 * Call all the functions and properties annotated with [EnumeratedRoute]
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
fun Route.applyEnumeratedRoute(
    vararg arguments: Any?
): List<Any?> {
    return applyWith<EnumeratedRoute>(*arguments)
}

/**
 * Call all the functions and properties annotated with [EnumeratedRoute]
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
fun Route.applyEnumeratedRoute(
    vararg arguments: Any?,
    predicate: (KCallable<*>) -> Boolean
): List<Any?> {
    return applyWith<EnumeratedRoute>(*arguments, predicate = predicate)
}

//////////////////////////////////////////////////

/**
 * The default ktor plugin enumeration annotation.
 *
 * For structures without custom annotations.
 *
 * @author LSafer
 * @since 1.0.0
 */
@Enumerable
@EnumerableParameters(Application::class)
@EnumerableReturnType(Unit::class)
@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class EnumeratedApplication(
    /**
     * The enumeration qualifier.
     *
     * @since 1.0.0
     */
    val value: String = "",
    /**
     * Used to reduce conflict between multiple modules.
     *
     * @since 1.0.0
     */
    val domain: String = ""
)

/**
 * Call all the functions and properties annotated with [EnumeratedApplication]
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
fun Application.applyEnumeratedApplication(
    vararg arguments: Any?
): List<Any?> {
    return applyWith<EnumeratedApplication>(*arguments)
}

/**
 * Call all the functions and properties annotated with [EnumeratedApplication]
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
fun Application.applyEnumeratedApplication(
    vararg arguments: Any?,
    predicate: (KCallable<*>) -> Boolean
): List<Any?> {
    return applyWith<EnumeratedApplication>(*arguments, predicate = predicate)
}

//////////////////////////////////////////////////
