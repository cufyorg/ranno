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
import org.cufy.ranno.EnumerableParameters

/**
 * Perform [EnumerableParameters] validation on the given [elements]
 *
 * Elements that fails the validation will be removed from [elements]
 * and reported to `environment`
 */
fun ProcessingContext.validateParameters(elements: MutableList<KSAnnotated>) {
    val vParametersList = getParametersRestrictions()

    elements.retainAll { element ->
        // resolve the parameters of `element`
        val eParameters = getParametersInformationOfOrNull(element)
                ?: /* skip validation */ return@retainAll true

        // ensure the `eParameters` matches all `vParametersList`
        vParametersList.all { vParameters ->
            // size matching
            if (eParameters.size != vParameters.size) {
                val en = getRepresentableName(element)
                val epc = eParameters.size
                val vpc = vParameters.size
                val an = annotation.simpleName.asString()
                val message = formatParametersSizeMismatchError(en, epc, vpc, an)
                environment.logger.error(message, element)
            }

            // match each item in `eParameters` with `vParameters`
            eParameters.zip(vParameters).withIndex().all all1@{ (pp, pair) ->
                val eParameterNode = pair.first.first
                val eParameter = pair.first.second
                val vParameter = pair.second

                if (!eParameter.isAssignableFrom(vParameter)) {
                    val en = getRepresentableName(element)
                    val vpn = vParameter.declaration.simpleName.asString()
                    val an = annotation.simpleName.asString()
                    val message = formatParameterError(en, pp, vpn, an)
                    environment.logger.error(message, eParameterNode)
                    return@all1 false
                }

                true
            }
        }
    }
}

private fun ProcessingContext.getParametersRestrictions(): Sequence<List<KSType>> {
    return annotation.findKSAnnotationsByType(EnumerableParameters::class).mapNotNull {
        it.findArgument<List<KSType>>() ?: run {
            environment.logger.error("value was not provided", it)
            null
        }
    }
}

private fun ProcessingContext.getParametersInformationOfOrNull(element: KSAnnotated): List<Pair<KSNode, KSType>>? {
    return when (element) {
        is KSFunctionDeclaration -> {
            val elementReceiversTuples = listOfNotNull(
                element.parentDeclaration
                    ?.let { it as? KSClassDeclaration }
                    ?.let { it to it.asStarProjectedType() },
                element.extensionReceiver
                    ?.let { it to it.resolve() }
            )

            val elementParametersTuples = element.parameters
                .map { it to it.type.resolve() }

            elementReceiversTuples + elementParametersTuples
        }
        is KSPropertyDeclaration -> {
            val elementReceiversTuples = listOfNotNull(
                element.parentDeclaration
                    ?.let { it as? KSClassDeclaration }
                    ?.let { it to it.asStarProjectedType() },
                element.extensionReceiver
                    ?.let { it to it.resolve() }
            )

            elementReceiversTuples
        }
        else -> null
    }
}

private fun formatParametersSizeMismatchError(
    en: String, // element name
    epc: Int, // element parameters count
    vpc: Int, // validator parameters count
    an: String // annotation name
) = "The parameters count of '$en' must be $vpc as per '@$an' but was $epc"

private fun formatParameterError(
    en: String, // element name
    pp: Int, // parameter position
    vpn: String, // validator parameter type name
    an: String // annotation name
) = "The type of the $pp-th parameter of '$en' must be assignable to '$vpn' as per '@$an'"
