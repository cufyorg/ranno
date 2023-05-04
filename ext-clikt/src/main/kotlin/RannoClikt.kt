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
import org.cufy.ranno.*
import org.intellij.lang.annotations.Language
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations

//////////////////////////////////////////////////

/**
 * Return all the commands annotated with [T].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun <T : Annotation> commandsWith(annotation: KClass<T>): List<CliktCommand> {
    return classesWith(annotation)
        .asSequence()
        .map { it.objectInstance ?: it.createInstance() }
        .filterIsInstance<CliktCommand>()
        .toList()
}

/**
 * Return all the commands annotated with [T]
 * and one of the annotations matches the given [predicate].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun <T : Annotation> commandsWith(annotation: KClass<T>, predicate: (KClass<*>) -> Boolean): List<CliktCommand> {
    return classesWith(annotation).filter(predicate)
        .asSequence()
        .map { it.objectInstance ?: it.createInstance() }
        .filterIsInstance<CliktCommand>()
        .toList()
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
inline fun <reified T : Annotation> commandsWith(): List<CliktCommand> {
    return commandsWith(T::class)
}

/**
 * Return all the commands annotated with [T]
 * and one of the annotations matches the given [predicate].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
inline fun <reified T : Annotation> commandsWith(noinline predicate: (KClass<*>) -> Boolean): List<CliktCommand> {
    return commandsWith(T::class, predicate)
}

//////////////////////////////////////////////////

/**
 * Run the commands annotated with [T] (see [CliktCommand.main])
 * with the given [arguments].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun <T : Annotation> runCommandWith(annotation: KClass<T>, vararg arguments: String) {
    commandsWith(annotation)
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
fun <T : Annotation> runCommandWith(annotation: KClass<T>, vararg arguments: String, predicate: (CliktCommand) -> Boolean) {
    commandsWith(annotation)
        .filter { predicate(it) }
        .forEach { it.parse(arguments.asList()) }
}

/**
 * Run the commands annotated with [T] (see [CliktCommand.main])
 * with the given [arguments].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
inline fun <reified T : Annotation> runCommandWith(vararg arguments: String) {
    runCommandWith(T::class, *arguments)
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
inline fun <reified T : Annotation> runCommandWith(vararg arguments: String, noinline predicate: (CliktCommand) -> Boolean) {
    runCommandWith(T::class, *arguments, predicate = predicate)
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

/**
 * Return all the commands annotated with [EnumeratedCommand].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun enumeratedCommands(): List<CliktCommand> {
    return commandsWith<EnumeratedCommand>()
}

/**
 * Return all the commands annotated with [EnumeratedCommand]
 * and one of the annotations matches the given [predicate].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun enumeratedCommands(predicate: (CliktCommand) -> Boolean): List<CliktCommand> {
    return commandsWith<EnumeratedCommand>().filter(predicate)
}

/**
 * Return all the commands annotated with [EnumeratedCommand]
 * and matches the given [name] regexp and [domain] regexp.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun enumeratedCommands(
    @Language("RegExp") name: String = ".*",
    @Language("RegExp") domain: String = ".*"
): List<CliktCommand> {
    val n = name.toRegex()
    val d = domain.toRegex()
    return commandsWith<EnumeratedCommand> {
        it.findAnnotations<EnumeratedCommand>()
            .any { it.name matches n && it.domain matches d }
    }
}

//////////////////////////////////////////////////

/**
 * Run the commands annotated with [EnumeratedCommand] (see [CliktCommand.main])
 * with the given [arguments].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun runEnumeratedCommands(vararg arguments: String) {
    enumeratedCommands().forEach { it.parse(arguments.asList()) }
}

/**
 * Run the commands annotated with [EnumeratedCommand] (see [CliktCommand.main])
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
fun runEnumeratedCommands(vararg arguments: String, predicate: (CliktCommand) -> Boolean) {
    enumeratedCommands(predicate).forEach { it.parse(arguments.asList()) }
}

/**
 * Run the commands annotated with [EnumeratedCommand] (see [CliktCommand.main])
 * and matches the given [name] regexp and [domain] regexp
 * with the given [arguments].
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @param arguments the arguments.
 * @since 1.0.0
 */
fun runEnumeratedCommands(
    vararg arguments: String,
    @Language("RegExp") name: String = ".*",
    @Language("RegExp") domain: String = ".*"
) {
    enumeratedCommands(name, domain).forEach { it.main(arguments) }
}

//////////////////////////////////////////////////
