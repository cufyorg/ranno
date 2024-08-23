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

/*
Notes on java resources:
- a resource tree can exist in multiple places
- a resource tree can exist in a jar
- a resource tree can exist in a plain directory
*/

/**
 * Try reading the lines in this uri, or return an empty
 * sequence on failure.
 */
internal fun URI.readLinesOrEmpty(): Sequence<String> {
    return try {
        toURL().openStream().reader().readLines().asSequence()
    } catch (e: Exception) {
        emptySequence()
    }
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

internal fun createSubUri(parent: URI, child: String): URI {
    val parentString = parent.toString().removeSuffix("/")
    return URI.create("$parentString/$child")
}
