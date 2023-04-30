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
package org.cufy.ranno.internal

import java.lang.ClassLoader.getSystemResources
import java.lang.reflect.Array
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.jvmErasure

/**
 * Assuming `this` class is a file class (e.g. UtilsKt)
 * find all the methods in it.
 *
 * A workaround for toplevel reflection
 */
internal val KClass<*>.jvmMethods: Sequence<Method>
    get() = java.nestMembers.single().let {
        sequenceOf(it.methods, it.declaredMethods)
            .flatMap { it.asSequence() }.distinct()
    }

/**
 * Assuming `this` class is a file class (e.g. UtilsKt)
 * find all the fields in it.
 *
 * A workaround for toplevel reflection
 */
internal val KClass<*>.jvmFields: Sequence<Field>
    get() = java.nestMembers.single().let {
        sequenceOf(it.fields, it.declaredFields)
            .flatMap { it.asSequence() }.distinct()
    }

/**
 * A type-safe function to obtain the `qualifiedName` of
 * this class or throw a descriptive error on failure.
 */
internal val KClass<*>.qualifiedNameOrThrow: String
    get() = qualifiedName ?: error("Cannot get qualified name of $this")

/**
 * Return a list containing the lines of the resource with the given [name].
 *
 * If the resource is a directory, the returned list will be the names of the files in it.
 */
internal fun resource(name: String): List<String> {
    return getSystemResources(name)
        .asSequence()
        .flatMap {
            it.openConnection()
                .getInputStream()
                .bufferedReader()
                .use { it.readLines() }
        }
        .toList()
}

/**
 * Return a list containing the paths of the resources in the directory with the given [name].
 *
 * If the resource is not a directory, the behaviour of this function is undefined.
 */
internal fun resourceTree(name: String): List<String> {
    return resource(name).map { "$name/$it" }
}

/**
 * For a class `A` return the array variant of it `A[]`.
 */
internal fun KClass<*>.asArrayClass(): KClass<*> {
    return Array.newInstance(java, 0)::class
}

/**
 * Decode the jvm parameters signature [classnames] into a list of k-classes.
 *
 * Example:
 * ```
 * ILjava.lang.String;Z -> listOf(Int::class, String::class, Boolean::class)
 * ```
 */
internal fun decodeClassnames(classnames: String): List<KClass<*>>? {
    var i = 0

    fun hasNext(): Boolean {
        return i < classnames.length
    }

    fun decodeNext(): KClass<*>? {
        return when (classnames[i++]) {
            'Z' -> Boolean::class
            'B' -> Byte::class
            'C' -> Char::class
            'D' -> Double::class
            'F' -> Float::class
            'I' -> Int::class
            'J' -> Long::class
            'S' -> Short::class
            '[' -> decodeNext()?.asArrayClass()
            'L' -> {
                val terminal = classnames.indexOf(';', startIndex = i)
                val classname = classnames.substring(i, terminal)
                i = terminal + 1
                lookupClass(classname.replace("/", "."))
                        ?: return null
            }
            else -> error("Malformed parameters signature $classnames")
        }
    }

    return buildList {
        while (hasNext()) {
            add(decodeNext() ?: return null)
        }
    }
}

internal fun KCallable<*>.canApply(vararg arguments: Any?): Boolean {
    if (parameters.size != arguments.size)
        return false

    for (i in parameters.indices) {
        val parameter = parameters[i]
        val argument = arguments[i]

        if (!parameter.type.jvmErasure.isInstance(argument))
            return false
    }

    return true
}
