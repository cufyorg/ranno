@file:JvmName("RannoKt")
@file:JvmMultifileClass

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

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.jvmErasure

//////////////////////////////////////////////////

/**
 * Return all the effectively static values annotated
 * with [annotation] qualified name.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun valuesWith(annotation: String): List<Any?> {
    return propertiesWith(annotation)
        .mapNotNull { it.parameterlessGetterOrNull() }
        .map { it() }
}

/**
 * Return all the effectively static values annotated
 * with [annotation] qualified name.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun valuesWith(annotation: KClass<out Annotation>): List<Any?> {
    return propertiesWith(annotation)
        .mapNotNull { it.parameterlessGetterOrNull() }
        .map { it() }
}

/**
 * Return all the effectively static values annotated
 * with [annotation] qualified name.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
fun valuesWith(annotation: Annotation): List<Any?> {
    return propertiesWith(annotation)
        .mapNotNull { it.parameterlessGetterOrNull() }
        .map { it() }
}

/**
 * Return all the effectively static values annotated
 * with [T] qualified name.
 *
 * ___Note: Only the elements passed to ranno
 * annotation processor will be returned by this
 * function.___
 *
 * @since 1.0.0
 */
inline fun <reified T : Annotation> valuesWith(): Map<Any?, List<T>> {
    return buildMap {
        for ((property, annotations) in propertiesWith<T>()) {
            val parameterlessGetter = property.parameterlessGetterOrNull() ?: continue

            put(parameterlessGetter(), annotations)
        }
    }
}

//////////////////////////////////////////////////

@PublishedApi
internal fun KProperty<*>.parameterlessGetterOrNull(): (() -> Any?)? {
    val ins = this.instanceParameter
    val ext = this.extensionReceiverParameter

    val insObj = ins?.let { it.type.jvmErasure.objectInstance ?: return null }
    val extObj = ext?.let { it.type.jvmErasure.objectInstance ?: return null }

    return when {
        ins != null && ext != null -> ({ getter.call(insObj, extObj) })
        ins != null && ext == null -> ({ getter.call(insObj) })
        ins == null && ext != null -> ({ getter.call(extObj) })
        else -> ({ getter.call() })
    }
}

//////////////////////////////////////////////////
