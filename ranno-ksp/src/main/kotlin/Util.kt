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

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.impl.symbol.kotlin.KSFunctionDeclarationImpl
import com.google.devtools.ksp.impl.symbol.kotlin.KSPropertyDeclarationImpl
import com.google.devtools.ksp.impl.symbol.kotlin.KSTypeImpl
import com.google.devtools.ksp.processing.Resolver
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

/** This does not include suspend continuation argument */
val KSFunctionDeclaration.jvmParameters: List<KSType>
    get() = buildList {
        // parent
        parentDeclaration
            ?.let { it as? KSClassDeclaration }
            ?.asStarProjectedType()
            ?.let { add(it) }

        // context parameters
        addAll(getContextParameters())

        // extension receiver
        extensionReceiver
            ?.resolve()
            ?.let { add(it) }

        // value parameters
        addAll(parameters.map { it.type.resolve() })
    }

val KSPropertyDeclaration.jvmParameters: List<KSType>
    get() = buildList {
        // parent
        parentDeclaration
            ?.let { it as? KSClassDeclaration }
            ?.asStarProjectedType()
            ?.let { add(it) }

        // context parameters
        addAll(getContextParameters())

        // extension receiver
        extensionReceiver
            ?.resolve()
            ?.let { add(it) }
    }

@OptIn(KspExperimental::class)
fun Resolver.mapToActualJvmSignature(declaration: KSDeclaration): String? {
    val actual = when (declaration) {
        is KSTypeAlias -> declaration.findActualType()
        else -> declaration
    }
    return mapToJvmSignature(actual)
}

private fun KSTypeAlias.findActualType(): KSClassDeclaration {
    val resolvedType = this.type.resolve().declaration
    return if (resolvedType is KSTypeAlias) {
        resolvedType.findActualType()
    } else {
        resolvedType as KSClassDeclaration
    }
}

@Suppress("UNCHECKED_CAST", "JAVA_CLASS_ON_COMPANION")
@OptIn(
    org.jetbrains.kotlin.analysis.api.KaExperimentalApi::class,
    ksp.org.jetbrains.kotlin.analysis.api.KaExperimentalApi::class,
    org.jetbrains.kotlin.analysis.api.KaImplementationDetail::class,
    ksp.org.jetbrains.kotlin.analysis.api.KaImplementationDetail::class
)
fun KSFunctionDeclaration.getContextParameters(): List<KSType> {
    try {
        if (this !is KSFunctionDeclarationImpl) return emptyList()
        val ktFunctionSymbol = this.javaClass
            .getDeclaredField("ktFunctionSymbol")
            .also { it.isAccessible = true }
            .get(this)
        if (ktFunctionSymbol is org.jetbrains.kotlin.analysis.api.symbols.markers.KaContextParameterOwnerSymbol)
            return ktFunctionSymbol.contextParameters.map {
                KSTypeImpl.javaClass
                    .getMethod("getCached", org.jetbrains.kotlin.analysis.api.types.KaType::class.java)
                    .invoke(KSTypeImpl, it.returnType) as KSType
            }

        if (ktFunctionSymbol is ksp.org.jetbrains.kotlin.analysis.api.symbols.markers.KaContextParameterOwnerSymbol)
            return ktFunctionSymbol.contextParameters.map {
                KSTypeImpl.javaClass
                    .getMethod("getCached", ksp.org.jetbrains.kotlin.analysis.api.types.KaType::class.java)
                    .invoke(KSTypeImpl, it.returnType) as KSType
            }
        return emptyList()
    } catch (_: Throwable) {
        return emptyList()
    }
}

@Suppress("UNCHECKED_CAST", "JAVA_CLASS_ON_COMPANION")
@OptIn(
    org.jetbrains.kotlin.analysis.api.KaExperimentalApi::class,
    ksp.org.jetbrains.kotlin.analysis.api.KaExperimentalApi::class,
    org.jetbrains.kotlin.analysis.api.KaImplementationDetail::class,
    ksp.org.jetbrains.kotlin.analysis.api.KaImplementationDetail::class
)
private fun KSPropertyDeclaration.getContextParameters(): List<KSType> {
    try {
        if (this !is KSPropertyDeclarationImpl) return emptyList()
        val ktPropertySymbol = this.javaClass
            .getDeclaredField("ktPropertySymbol")
            .also { it.isAccessible = true }
            .get(this)
        if (ktPropertySymbol is org.jetbrains.kotlin.analysis.api.symbols.markers.KaContextParameterOwnerSymbol)
            return ktPropertySymbol.contextParameters.map {
                KSTypeImpl.javaClass
                    .getMethod("getCached", org.jetbrains.kotlin.analysis.api.types.KaType::class.java)
                    .invoke(KSTypeImpl, it.returnType) as KSType
            }

        if (ktPropertySymbol is ksp.org.jetbrains.kotlin.analysis.api.symbols.markers.KaContextParameterOwnerSymbol)
            return ktPropertySymbol.contextParameters.map {
                KSTypeImpl.javaClass
                    .getMethod("getCached", ksp.org.jetbrains.kotlin.analysis.api.types.KaType::class.java)
                    .invoke(KSTypeImpl, it.returnType) as KSType
            }
        return emptyList()
    } catch (_: Throwable) {
        return emptyList()
    }
}
