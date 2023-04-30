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
import kotlin.reflect.KClass

internal fun KSAnnotated.findKSAnnotationsByType(annotationClass: KClass<*>): Sequence<KSAnnotation> {
    return annotations.filter {
        it.shortName.getShortName() ==
                annotationClass.simpleName &&
                it.annotationType.resolve().declaration.qualifiedName?.asString() ==
                annotationClass.qualifiedName
    }
}

internal inline fun <reified T> KSAnnotation.findArgument(name: String = "value"): T? {
    return arguments.find { it.name?.asString() == name }?.value as? T
}

/**
 * Return a name of [element] that can be printed for debuging.
 */
fun ProcessingContext.getRepresentableName(element: KSAnnotated): String {
    return when (element) {
        is KSDeclaration -> element.simpleName.asString()
        is KSPropertyAccessor -> element.receiver.simpleName.asString()
        else -> "<${element::class}>"
    }
}

/**
 * Return a name of this that can be printed for debuting.
 */
val KSAnnotated.representableName: String
    get() = when (this) {
        is KSDeclaration -> simpleName.asString()
        is KSPropertyAccessor -> receiver.simpleName.asString()
        else -> "<${this::class}>"
    }

val KSFunctionDeclaration.jvmParameters: List<KSType>
    get() {
        val receivers = listOfNotNull(
            parentDeclaration
                ?.let { it as? KSClassDeclaration }
                ?.asStarProjectedType(),
            extensionReceiver
                ?.resolve()
        )
        val parameters = parameters.map {
            it.type.resolve()
        }

        return receivers + parameters
    }

val KSPropertyDeclaration.jvmParameters: List<KSType>
    get() {
        return listOfNotNull(
            parentDeclaration
                ?.let { it as? KSClassDeclaration }
                ?.asStarProjectedType(),
            extensionReceiver
                ?.resolve()
        )
    }
