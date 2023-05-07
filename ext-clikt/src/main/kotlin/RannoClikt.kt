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
package org.cufy.ranno.clikt

import com.github.ajalt.clikt.core.CliktCommand
import org.cufy.ranno.Enumerable
import org.cufy.ranno.EnumerableSuperType
import org.cufy.ranno.classesWith
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations

//////////////////////////////////////////////////

/**
 * Return all the commands annotated with [annotation] qualified name.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun commandsWith(annotation: String, predicate: (KClass<*>) -> Boolean = { true }): List<CliktCommand> {
    return classesWith(annotation).mapMaybeCommand(predicate)
}

/**
 * Return all the commands annotated with [annotation].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun commandsWith(annotation: KClass<out Annotation>, predicate: (KClass<*>) -> Boolean = { true }): List<CliktCommand> {
    return classesWith(annotation).mapMaybeCommand(predicate)
}

/**
 * Return all the commands annotated with [T].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
inline fun <reified T : Annotation> commandsWith(noinline predicate: (T) -> Boolean = { true }): List<CliktCommand> {
    return commandsWith(T::class) { it.findAnnotations<T>().any(predicate) }
}

/**
 * Return all the commands annotated with [annotation].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun commandsWith(annotation: Annotation): List<CliktCommand> {
    return classesWith(annotation).mapMaybeCommand()
}

//////////////////////////////////////////////////

/**
 * Run the commands annotated with [annotation] (see [CliktCommand.main])
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun runCommandWith(annotation: String, vararg arguments: String, predicate: (CliktCommand) -> Boolean) {
    commandsWith(annotation)
        .filter { predicate(it) }
        .forEach { it.parse(arguments.asList()) }
}

/**
 * Run the commands annotated with [annotation] (see [CliktCommand.main])
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun runCommandWith(annotation: KClass<out Annotation>, vararg arguments: String, predicate: (CliktCommand) -> Boolean) {
    commandsWith(annotation)
        .filter { predicate(it) }
        .forEach { it.parse(arguments.asList()) }
}

/**
 * Run the commands annotated with [T] (see [CliktCommand.main])
 * and matches the given [predicate]
 * with the given [arguments].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
inline fun <reified T : Annotation> runCommandWith(vararg arguments: String, crossinline predicate: (T) -> Boolean) {
    runCommandWith(T::class, *arguments) { it::class.findAnnotations<T>().any(predicate) }
}

/**
 * Run the commands annotated with [annotation] (see [CliktCommand.main])
 * with the given [arguments].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun runCommandWith(annotation: Annotation, vararg arguments: String) {
    commandsWith(annotation)
        .forEach { it.parse(arguments.asList()) }
}

//////////////////////////////////////////////////

/**
 * The default clikt enumeration annotation.
 *
 * For structures without custom annotations.
 *
 * @author LSafer
 * @since 1.0.0
 */
@Enumerable
@Repeatable
@EnumerableSuperType(CliktCommand::class)
@Target(AnnotationTarget.CLASS)
annotation class EnumeratedCommand(
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

private inline fun Iterable<KClass<*>>.mapMaybeCommand(
    crossinline predicate: (KClass<*>) -> Boolean = { true }
): List<CliktCommand> {
    return asSequence()
        .filter { predicate(it) }
        .map { it.objectInstance ?: it.createInstance() }
        .filterIsInstance<CliktCommand>()
        .toList()
}

//////////////////////////////////////////////////
