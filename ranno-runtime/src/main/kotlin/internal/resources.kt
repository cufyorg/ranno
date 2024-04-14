/*
 *	Copyright 2023 cufy.org and meemer.com
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

import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Files
import kotlin.streams.asSequence

/*
Notes on java resources:
- a resource tree can exist in multiple places
- a resource tree can exist in a jar
- a resource tree can exist in a plain directory
*/

/**
 * Get all the locations of the resource with the
 * given [name].
 */
internal fun listResourceLocations(name: String): List<URI> {
    return ClassLoader.getSystemResources(name).asSequence().map { it.toURI() }.toList()
}

internal fun createSubUri(parent: URI, child: String): URI {
    val parentString = parent.toString().removeSuffix("/")
    return URI.create("$parentString/$child")
}

internal fun URI.listUris(): List<URI> {
    return list().map { createSubUri(this, it) }
}

internal fun URI.list(): List<String> {
    @Suppress("IntroduceWhenSubject")
    return when {
        scheme == "jar" -> jarList()
        scheme == "file" -> fileList()
        else -> emptyList()
    }
}

private fun URI.jarList(): List<String> {
    require(scheme == "jar")
    return useFilesystem { filesystem ->
        val path = toString().substringAfter("!")
        val resourcePath = filesystem.getPath(path)
        Files.walk(resourcePath, 1)
            .asSequence()
            .drop(1)
            .map { it.fileName.toString() }
            .toList()
    }
}

private fun URI.fileList(): List<String> {
    require(scheme == "file")
    return readLines().toList()
}

internal fun URI.readLines(): Sequence<String> {
    return toURL().openStream().reader().readLines().asSequence()
}

internal fun <T> URI.useFilesystem(block: (FileSystem) -> T): T {
    return try {
        // if a filesystem exists, use it but don't close it (It's not ours)
        val filesystem = FileSystems.getFileSystem(this)
        block(filesystem)
    } catch (_: FileSystemNotFoundException) {
        // if not, create a temporary one and close right after usage.
        val filesystem = FileSystems.newFileSystem(this, mutableMapOf<String?, Any?>())
        filesystem.use(block)
    }
}
