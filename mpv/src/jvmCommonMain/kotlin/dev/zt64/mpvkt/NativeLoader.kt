package dev.zt64.mpvkt

import java.nio.file.Files

internal object NativeLoader {
    fun loadLibrary(classLoader: ClassLoader, libName: String) {
        val url = classLoader.getResource("libs/${libFilename(libName)}")
        val file = Files.createTempFile("jni", libFilename(nameOnly(libName))).toFile().apply {
            deleteOnExit()
            delete()
        }
        url!!.openStream().use { Files.copy(it, file.toPath()) }
        System.load(file.canonicalPath)
    }

    private fun libFilename(libName: String): String {
        val osName = System.getProperty("os.name").lowercase()
        return when {
            osName.contains("win") -> "$libName.dll"
            osName.contains("mac") -> decorateLibraryName(libName, ".dylib")
            else -> decorateLibraryName(libName, ".so")
        }
    }

    private fun nameOnly(libName: String): String {
        return libName.substringAfterLast('/')
    }

    private fun decorateLibraryName(libraryName: String, suffix: String): String {
        if (libraryName.endsWith(suffix)) return libraryName
        val prefix = libraryName.substringBeforeLast('/', "")
        val name = libraryName.substringAfterLast('/')
        return "${prefix}lib$name$suffix"
    }
}