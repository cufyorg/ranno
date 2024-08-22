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

import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf

/**
 * Return true if this function can be call with the given [parameters]
 * and returns instance of [returnType].
 *
 * @param suspend pass true to match suspend functions
 */
fun KFunction<*>.matchSignature(returnType: KType, vararg parameters: KType, suspend: Boolean = false): Boolean {
    return matchSignature(returnType, parameters.asList(), suspend)
}

/**
 * Return true if this function can be call with the given [parameters]
 * and returns instance of [returnType].
 *
 * @param suspend pass true to match suspend functions
 */
fun KFunction<*>.matchSignature(returnType: KType, parameters: List<KType>, suspend: Boolean = false): Boolean {
    if (!suspend && this.isSuspend)
        return false

    if (!this.returnType.isSubtypeOf(returnType))
        return false

    if (this.parameters.size != parameters.size)
        return false

    for (i in this.parameters.indices)
        if (!this.parameters[i].type.isSupertypeOf(parameters[i]))
            return false

    return true
}
