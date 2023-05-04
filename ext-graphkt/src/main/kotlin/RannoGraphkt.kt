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
import org.cufy.ranno.*
import org.intellij.lang.annotations.Language
import kotlin.reflect.KCallable
import kotlin.reflect.full.findAnnotations

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
    val name: String = "",
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
fun GraphQLRoute<*>.applyEnumeratedGraphQLRoute(vararg arguments: Any?): List<Any?> {
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
fun GraphQLRoute<*>.applyEnumeratedGraphQLRoute(vararg arguments: Any?, predicate: (KCallable<*>) -> Boolean): List<Any?> {
    return applyWith<EnumeratedGraphQLRoute>(*arguments, predicate = predicate)
}

/**
 * Call all the functions and properties annotated with [EnumeratedGraphQLRoute]
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
fun Any.applyEnumeratedGraphQLRoute(
    vararg arguments: Any?,
    @Language("RegExp") name: String = ".*",
    @Language("RegExp") domain: String = ".*"
): List<Any?> {
    val n = name.toRegex()
    val d = domain.toRegex()
    return applyWith<EnumeratedGraphQLRoute>(*arguments) {
        it.findAnnotations<EnumeratedGraphQLRoute>()
            .any { it.name matches n && it.domain matches d }
    }
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
    val name: String = "",
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
fun GraphQLSchemaBuilder.applyEnumeratedGraphQLSchema(vararg arguments: Any?): List<Any?> {
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
fun GraphQLSchemaBuilder.applyEnumeratedGraphQLSchema(vararg arguments: Any?, predicate: (KCallable<*>) -> Boolean): List<Any?> {
    return applyWith<EnumeratedGraphQLRoute>(*arguments, predicate = predicate)
}

/**
 * Call all the functions and properties annotated with [EnumeratedGraphQLSchema]
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
fun Any.applyEnumeratedGraphQLSchema(
    vararg arguments: Any?,
    @Language("RegExp") name: String = ".*",
    @Language("RegExp") domain: String = ".*"
): List<Any?> {
    val n = name.toRegex()
    val d = domain.toRegex()
    return applyWith<EnumeratedGraphQLSchema>(*arguments) {
        it.findAnnotations<EnumeratedGraphQLSchema>()
            .any { it.name matches n && it.domain matches d }
    }
}
