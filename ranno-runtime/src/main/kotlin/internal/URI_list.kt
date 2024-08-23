package org.cufy.ranno.internal

import java.net.URI
import java.nio.file.Files
import java.nio.file.spi.FileSystemProvider
import java.util.jar.JarFile
import kotlin.streams.asSequence

internal fun URI.listUrisOrEmpty(): List<URI> {
    return listOrEmpty().map { createSubUri(this, it) }
}

internal fun URI.listOrEmpty(): List<String> {
    @Suppress("IntroduceWhenSubject")
    return when {
        scheme == "jar" -> jar_listOrEmpty()
        scheme == "file" -> file_listOrEmpty()
        else -> emptyList()
    }
}

@Suppress("FunctionName")
private fun URI.jar_listOrEmpty(): List<String> {
    require(scheme == "jar")

    val pathname = this.toString()
        .removePrefix("jar:")
        .removePrefix("file:")

    // if jar file system is supported (aka ZipFileSystemProvider is present)
    if (FileSystemProvider.installedProviders().any { it.scheme == "jar" }) {
        return useFilesystem { filesystem ->
            val relativePathname = pathname.substringAfter("!")
            val resourcePath = filesystem.getPath(relativePathname)
            Files.walk(resourcePath, 1)
                .asSequence()
                .drop(1)
                .map { it.fileName.toString() }
                .toList()
        }
    }

    // Second approach
    // Inspiration: https://github.com/kherge/java.resource/blob/master/src/main/java/io/herrera/kevin/resource/Resource.java
    val physicalPathname = pathname.substringBefore("!/")
    val folder = pathname.substringAfter("!/")
        .let { if (it.endsWith("/")) it else "$it/" }

    val resourceFile = JarFile(physicalPathname)

    return resourceFile.use {
        // stream all the files in the jar (java streams API)
        it.stream().asSequence()
            // name length check is a lighter way for excluding the folder itself from the results
            .filter { it.name.length > folder.length && it.name.startsWith(folder) }
            // transform, stripping the folder name to end up with just the last segment of the path
            .map { it.name.removePrefix(folder) }
            .toList()
    }
}

@Suppress("FunctionName")
private fun URI.file_listOrEmpty(): List<String> {
    require(scheme == "file")
    return readLinesOrEmpty().toList()
}
