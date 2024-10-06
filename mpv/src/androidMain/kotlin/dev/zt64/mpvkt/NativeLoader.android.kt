package dev.zt64.mpvkt

internal actual fun loadNativeLibrary(libName: String) {
    System.loadLibrary(libName)
}