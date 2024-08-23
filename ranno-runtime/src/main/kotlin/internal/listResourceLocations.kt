package org.cufy.ranno.internal

import org.cufy.ranno.Enumerable
import org.cufy.ranno.Enumerated
import java.net.URI

/**
 * Get all the locations of the resource with the
 * given [name].
 */
internal fun listResourceLocations(name: String): List<URI> {
    return buildList {
        addAll(listUsingSystemClassLoader(name))
        addAll(listUsingRannoClassLoader(name))
        addAll(listUsingBruteforceAOSP(name))
    }
}

private fun listUsingSystemClassLoader(name: String): List<URI> {
    return ClassLoader.getSystemResources(name)
        .asSequence()
        .map { it.toURI() }
        .toList()
}

private fun listUsingRannoClassLoader(name: String): List<URI> {
    return Enumerable::class.java.classLoader
        ?.getResources(name)
        ?.asSequence()
        .orEmpty()
        .map { it.toURI() }
        .toList()
}

/**
 * Special treatment just for AOSP (android), for some reason,
 * android is incapable of locating the location of a directory.
 * Thus, this approach trys (or return an empty list on failure)
 * to locate the location of a file that is sure to exist at runtime
 * and then locate the directory by searching relative to it.
 */
private fun listUsingBruteforceAOSP(name: String): List<URI> {
    val rootPathname = Enumerated::class.java.classLoader
        ?.getResource("AndroidManifest.xml")
        ?.toURI()
        ?.toString()
        ?.removeSuffix("AndroidManifest.xml")
        ?.removeSuffix("/")

    rootPathname ?: return emptyList()

    return listOf(URI("$rootPathname/$name"))
}
