package dev.zt64.mpvkt

internal actual fun loadNativeLibrary(libName: String) {
    NativeLoader.loadLibrary(LibMpv::class.java.classLoader!!, libName)
}