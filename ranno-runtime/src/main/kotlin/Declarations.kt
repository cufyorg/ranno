/*
 *	Copyright 2024 cufy.org
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
