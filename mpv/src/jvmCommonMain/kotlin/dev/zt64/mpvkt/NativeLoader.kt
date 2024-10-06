package dev.zt64.mpvkt

import java.io.IOException
import java.nio.file.Files

internal expect fun loadNativeLibrary(libName: String)

internal object NativeLoader {
    fun loadLibrary(name: String) {
        loadNativeLibrary(name)
    }

    fun loadLibrary(classLoader: ClassLoader, libName: String) {
        try {
            val url = classLoader.getResource("native/${libFilename(libName)}")

            val file = Files.createTempFile("$libName-", "-${libFilename(nameOnly(libName))}")
                .toFile()
                .apply {
                    deleteOnExit()
                    delete()
                }

            url!!.openStream().use { Files.copy(it, file.toPath()) }
            System.load(file.canonicalPath)
        } catch (e: Exception) {
            throw IOException("Could not load native $libName", e)
        }
    }

    private fun libFilename(libName: String): String {
        val osName = System.getProperty("os.name").lowercase()
        return when {
            "win" in osName -> "$libName.dll"
            "mac" in osName -> decorateLibraryName(libName, ".dylib")
            else -> decorateLibraryName(libName, ".so")
        }
    }

    private fun nameOnly(libName: String) = libName.substringAfterLast('/')

    private fun decorateLibraryName(libraryName: String, suffix: String): String {
        if (libraryName.endsWith(suffix)) return libraryName
        val prefix = libraryName.substringBeforeLast('/', "")
        val name = libraryName.substringAfterLast('/')
        return "${prefix}lib$name$suffix"
    }
}