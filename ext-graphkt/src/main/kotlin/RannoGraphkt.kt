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
