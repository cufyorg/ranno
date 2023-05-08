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
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import org.cufy.ranno.Enumerable
import java.util.UUID.randomUUID

class EnumerableSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EnumerableSymbolProcessor(environment)
    }
}

class EnumerableSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotations = resolver.obtainCustomAnnotations() +
                resolver.obtainBuiltinAnnotations() +
                resolver.obtainOptInAnnotations()

        annotations.distinct().forEach { annotation ->
            // ensure `annotation` is actually an annotation
            if (annotation.classKind != ClassKind.ANNOTATION_CLASS) {
                environment.logger.error("Only annotations are allowed to have $ANNOTATION_QN", annotation)
                return@forEach
            }

            val context = ProcessingContext(environment, resolver, annotation)

            context.processAnnotation()
        }

        return emptyList()
    }

    private fun Resolver.obtainCustomAnnotations(): Sequence<KSClassDeclaration> {
        return getSymbolsWithAnnotation(ANNOTATION_QN).filterIsInstance<KSClassDeclaration>()
    }

    private fun Resolver.obtainBuiltinAnnotations(): Sequence<KSClassDeclaration> {
        return BUILTIN_ANNOTATIONS_QN.asSequence()
            .mapNotNull { getClassDeclarationByName(it) }
    }

    private fun Resolver.obtainOptInAnnotations(): Sequence<KSClassDeclaration> {
        return environment.options[EXTERNAL_OPN].orEmpty().split(",", " ")
            .asSequence()
            .mapNotNull { getClassDeclarationByName(it.trim()) }
    }
}

//

private const val EXTERNAL_OPN = "ranno.external"
private val ANNOTATION_QN = Enumerable::class.qualifiedName!!
private val BUILTIN_ANNOTATIONS_QN = setOf(
    "org.cufy.ranno.Enumerated",
    "org.cufy.ranno.ktor.EnumeratedRoute",
    "org.cufy.ranno.ktor.EnumeratedApplication",
    "org.cufy.ranno.graphkt.EnumeratedGraphQLRoute",
    "org.cufy.ranno.graphkt.EnumeratedGraphQLSchema",
    "org.cufy.ranno.graphkt.EnumeratedGraphQLConfiguration",
    "org.cufy.ranno.clikt.EnumeratedCommand"
)

/**
 * A data object containing all the needed data
 * to process a single annotation anotatted with [Enumerable].
 */
data class ProcessingContext(
    val environment: SymbolProcessorEnvironment,
    val resolver: Resolver,
    val annotation: KSClassDeclaration
)

//

/**
 * Process the annotation in the receiver context.
 */
private fun ProcessingContext.processAnnotation() {
    // obtain annotation qualified name
    val qualifiedName = annotation.qualifiedName?.asString() ?: run {
        environment.logger.error("Only annotations with qualified name are allowed to have $ANNOTATION_QN", annotation)
        return
    }

    // get the elements annotated by `annotation` (not by @Enumerable)
    val elements = resolver.getSymbolsWithAnnotation(qualifiedName)
        .toMutableList()

    // early return
    if (elements.isEmpty()) return

    // validation (errors will be reported downstream)
    // and invalid elements will be removed from the list
    validateSuperType(elements)
    validateReturnType(elements)
    validateParameters(elements)

    // create file for signatures of `annotation`
    val out = environment.codeGenerator.createNewFileByPath(
        dependencies = Dependencies.ALL_FILES,
        path = ANNOTATION_QN + "/" + qualifiedName + "/" + randomUUID(),
        extensionName = ""
    )

    // write the signatures
    out.writer().use { writer ->
        elements.forEach { element ->
            // skip elements that cannot be processed (errors will be reported downstream)
            val signature = produceSignature(element) ?: return@forEach

            writer.write(signature)
            writer.write("\n")
        }
    }
}

//

/**
 * Produce signature for the given [element].
 *
 * Errors will be reported to the `environment`
 */
@OptIn(KspExperimental::class)
private fun ProcessingContext.produceSignature(element: KSAnnotated): String? {
    return when (element) {
        is KSClassDeclaration -> {
            val className = element.qualifiedName?.asString()
            "class $className"
        }
        is KSFunctionDeclaration -> {
            val className = resolver.getOwnerJvmClassName(element)
            val name = element.simpleName.asString()
            val parameters = element.jvmParameters.map {
                resolver.mapToJvmSignature(it.declaration)
            }.joinToString("")
            "function $className $name $parameters"
        }
        is KSPropertyDeclaration -> {
            val className = resolver.getOwnerJvmClassName(element)
            val name = element.simpleName.asString()
            val parameters = element.jvmParameters.map {
                resolver.mapToJvmSignature(it.declaration)
            }.joinToString("")
            "property $className $name $parameters"
        }
        else -> {
            environment.logger.error("Cannot produce signature for element", element)
            null
        }
    }
}
