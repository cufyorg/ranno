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

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

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
