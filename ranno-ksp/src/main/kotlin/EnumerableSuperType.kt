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
package org.cufy.ranno.ksp

import com.google.devtools.ksp.symbol.*
import org.cufy.ranno.EnumerableSuperType

/**
 * Perform [EnumerableSuperType] validation on the given [elements]
 *
 * Elements that fails the validation will be removed from [elements]
 * and reported to `environment`
 */
fun ProcessingContext.validateSuperType(elements: MutableList<KSAnnotated>) {
    val vTypeList = getSuperTypeRestrictions()

    elements.retainAll { element ->
        val (eTypeNode, eType) = getTypeInformationOfOrNull(element)
                ?: /* skip validation */ return@retainAll true

        vTypeList.all { vType ->
            if (!vType.isAssignableFrom(eType)) {
                val en = element.representableName
                val vtn = vType.declaration.simpleName.asString()
                val an = annotation.simpleName.asString()
                val message = formatSuperTypeError(en, vtn, an)
                environment.logger.error(message, eTypeNode)
                return@all false
            }

            true
        }
    }
}

private fun ProcessingContext.getSuperTypeRestrictions(): Sequence<KSType> {
    return annotation.findKSAnnotationsByType(EnumerableSuperType::class).mapNotNull {
        it.findArgument<KSType>() ?: run {
            environment.logger.error("value was not provided", it)
            null
        }
    }
}

private fun ProcessingContext.getTypeInformationOfOrNull(element: KSAnnotated): Pair<KSNode, KSType>? {
    return when (element) {
        is KSClassDeclaration -> element to element.asStarProjectedType()
        else -> null
    }
}

private fun formatSuperTypeError(
    en: String, // element name
    vtn: String, // validator type name
    an: String // annotation name
) = "class '$en' must extend '$vtn' as per '@$an'"
