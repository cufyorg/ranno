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

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.jvmErasure

/**
 * Find a class with the given [className] or `null`
 * if no such class exists.
 *
 * @return the found class. Or `null` if not found.
 */
internal fun lookupClass(className: String): KClass<*>? {
    return try {
        Class.forName(className).kotlin
    } catch (_: ClassNotFoundException) {
        null
    }
}

/**
 * Find a function in [klass] that has the given [name] and [parameters].
 *
 * @return the found function. Or `null` if not found.
 */
internal fun lookupFunction(klass: KClass<*>, name: String, parameters: List<KClass<*>>): KFunction<*>? {
    return try {
        klass.functions.first {
            !it.isSuspend &&
                    it.name == name &&
                    it.parameters.map { it.type.jvmErasure } == parameters
        }
    } catch (_: UnsupportedOperationException) {
        // workaround for toplevel reflection
        lookupToplevelFunction(klass, name, parameters)
    } catch (_: NoSuchElementException) {
        // workaround for toplevel reflection
        lookupToplevelFunction(klass, name, parameters)
    }
}

/**
 * Find a function in [klass] that has the given [name] and [parameters].
 *
 * @return the found function. Or `null` if not found.
 */
internal fun lookupSuspendFunction(klass: KClass<*>, name: String, parameters: List<KClass<*>>): KFunction<*>? {
    return try {
        klass.functions.first {
            it.isSuspend &&
                    it.name == name &&
                    it.parameters.map { it.type.jvmErasure } == parameters
        }
    } catch (_: UnsupportedOperationException) {
        // workaround for toplevel reflection
        lookupToplevelSuspendFunction(klass, name, parameters)
    } catch (_: NoSuchElementException) {
        // workaround for toplevel reflection
        lookupToplevelSuspendFunction(klass, name, parameters)
    }
}

/**
 * Find a property in [klass] that has the given [name] and [parameters].
 *
 * @return the found property. Or `null` if not found.
 */
internal fun lookupProperty(klass: KClass<*>, name: String, parameters: List<KClass<*>>): KProperty<*>? {
    return try {
        klass.members.filterIsInstance<KProperty<*>>().first {
            it.name == name &&
                    it.getter.parameters.map { it.type.jvmErasure } == parameters
        }
    } catch (_: UnsupportedOperationException) {
        // workaround for toplevel reflection
        lookupToplevelProperty(klass, name, parameters)
            ?: lookupToplevelPropertyNoBackingField(klass, name, parameters)
    } catch (_: NoSuchElementException) {
        // workaround for toplevel reflection
        lookupToplevelProperty(klass, name, parameters)
            ?: lookupToplevelPropertyNoBackingField(klass, name, parameters)
    }
}

/**
 * Find the element with the given ranno [signature].
 *
 * @return the found element. Or `null` if either the [signature] is invalid or the element not found.
 */
internal fun lookupElement(signature: String): KAnnotatedElement? {
    val splits = signature.split(" ")
    val type = splits.getOrElse(0) { "" }
    val classname = splits.getOrElse(1) { "" }
    val klass = lookupClass(classname) ?: return null
    return when (type) {
        "class" -> klass
        "function" -> {
            val name = splits.getOrElse(2) { "" }
            val parametersSignature = splits.getOrElse(3) { "" }
            val parameters = decodeClassnames(parametersSignature) ?: return null
            lookupFunction(klass, name, parameters)
        }

        "suspend-function" -> {
            val name = splits.getOrElse(2) { "" }
            val parametersSignature = splits.getOrElse(3) { "" }
            val parameters = decodeClassnames(parametersSignature) ?: return null
            lookupSuspendFunction(klass, name, parameters)
        }

        "property" -> {
            val name = splits.getOrElse(2) { "" }
            val parametersSignature = splits.getOrElse(3) { "" }
            val parameters = decodeClassnames(parametersSignature) ?: return null
            lookupProperty(klass, name, parameters)
        }

        else -> null
    }

}
