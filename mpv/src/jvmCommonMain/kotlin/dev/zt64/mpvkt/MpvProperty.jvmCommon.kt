package dev.zt64.mpvkt

public actual fun Mpv.getPropertyString(name: String): String? = LibMpv.getPropertyString(handle, name)

public actual fun Mpv.getPropertyFlag(name: String): Boolean? = LibMpv.getPropertyFlag(handle, name)

public actual fun Mpv.getPropertyLong(name: String): Long? = LibMpv.getPropertyLong(handle, name)

public actual fun Mpv.getPropertyDouble(name: String): Double? = LibMpv.getPropertyDouble(handle, name)

public actual fun Mpv.getPropertyArray(name: String): List<MpvNode>? = LibMpv.getPropertyArray(handle, name)?.asList()

public actual fun Mpv.getPropertyMap(name: String): Map<String, MpvNode>? = LibMpv.getPropertyMap(handle, name)

public actual fun Mpv.getPropertyByteArray(name: String): ByteArray? = TODO()

public actual fun Mpv.setOption(name: String, value: String) {
    LibMpv.setOption(handle, name, value)
}

public actual fun Mpv.setProperty(name: String, value: String) {
    LibMpv.setProperty(handle, name, value)
}

public actual fun Mpv.setProperty(name: String, value: Boolean) {
    LibMpv.setProperty(handle, name, value)
}

public actual fun Mpv.setProperty(name: String, value: Long) {
    LibMpv.setProperty(handle, name, value)
}

public actual fun Mpv.setProperty(name: String, value: Double) {
    LibMpv.setProperty(handle, name, value)
}

public actual fun Mpv.setProperty(name: String, value: List<MpvNode>) {
    TODO()
}

public actual fun Mpv.setProperty(name: String, value: Map<String, MpvNode>) {
    TODO()
}

public actual fun Mpv.setProperty(name: String, value: ByteArray) {
    TODO()
}

public actual fun <T> Mpv.observeProperty(name: String, callback: (T) -> Unit) {
    LibMpv.observeProperty(handle, generateReplyId(), name)
}

public actual fun Mpv.unobserveProperty(name: String) = LibMpv.unobserveProperty(handle, 0)

public actual fun Mpv.deleteProperty(name: String) = LibMpv.delProperty(handle, name)