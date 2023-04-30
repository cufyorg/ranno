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
package org.cufy.ranno.graphkt

import org.cufy.graphkt.schema.GraphQLRoute
import org.cufy.graphkt.schema.GraphQLSchemaBuilder
import org.cufy.ranno.Enumerable
import org.cufy.ranno.EnumerableParameters
import org.cufy.ranno.EnumerableReturnType
import org.cufy.ranno.applyWith
import kotlin.reflect.KCallable

/**
 * The default graphql route enumeration annotation.
 *
 * For structures without custom annotations.
 *
 * @author LSafer
 * @since 1.0.0
 */
@Enumerable
@EnumerableParameters(GraphQLRoute::class)
@EnumerableReturnType(Unit::class)
@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class EnumeratedGraphQLRoute(
    /**
     * The enumeration qualifier.
     *
     * @since 1.0.0
     */
    val value: String,
    /**
     * Used to reduce conflict between multiple modules.
     *
     * @since 1.0.0
     */
    val domain: String = ""
)

/**
 * Call all the functions and properties annotated with [EnumeratedGraphQLRoute]
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
fun GraphQLRoute<*>.applyEnumeratedGraphQLRoute(
    vararg arguments: Any?
): List<Any?> {
    return applyWith<EnumeratedGraphQLRoute>(*arguments)
}

/**
 * Call all the functions and properties annotated with [EnumeratedGraphQLRoute]
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
fun GraphQLRoute<*>.applyEnumeratedGraphQLRoute(
    vararg arguments: Any?,
    predicate: (KCallable<*>) -> Boolean
): List<Any?> {
    return applyWith<EnumeratedGraphQLRoute>(*arguments, predicate = predicate)
}

/**
 * The default graphql schema enumeration annotation.
 *
 * For structures without custom annotations.
 *
 * @author LSafer
 * @since 1.0.0
 */
@Enumerable
@EnumerableParameters(GraphQLSchemaBuilder::class)
@EnumerableReturnType(Unit::class)
@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class EnumeratedGraphQLSchema(
    /**
     * The enumeration qualifier.
     *
     * @since 1.0.0
     */
    val value: String,
    /**
     * Used to reduce conflict between multiple modules.
     *
     * @since 1.0.0
     */
    val domain: String = ""
)

/**
 * Call all the functions and properties annotated with [EnumeratedGraphQLSchema]
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
fun GraphQLSchemaBuilder.applyEnumeratedGraphQLSchema(
    vararg arguments: Any?
): List<Any?> {
    return applyWith<EnumeratedGraphQLRoute>(*arguments)
}

/**
 * Call all the functions and properties annotated with [EnumeratedGraphQLSchema]
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
fun GraphQLSchemaBuilder.applyEnumeratedGraphQLSchema(
    vararg arguments: Any?,
    predicate: (KCallable<*>) -> Boolean
): List<Any?> {
    return applyWith<EnumeratedGraphQLRoute>(*arguments, predicate = predicate)
}
