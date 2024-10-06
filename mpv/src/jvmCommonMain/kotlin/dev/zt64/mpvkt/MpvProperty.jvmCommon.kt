package dev.zt64.mpvkt

public actual fun Mpv.getPropertyString(name: String): String? {
    return LibMpv.getPropertyString(handle, name)
}

public actual fun Mpv.getPropertyFlag(name: String): Boolean? {
    return LibMpv.getPropertyFlag(handle, name)
}

public actual fun Mpv.getPropertyLong(name: String): Long? {
    return LibMpv.getPropertyLong(handle, name)
}

public actual fun Mpv.getPropertyDouble(name: String): Double? {
    return LibMpv.getPropertyDouble(handle, name)
}

public actual fun Mpv.getPropertyNode(name: String): MpvNode? {
    return LibMpv.getPropertyNode(handle, name)
}

public actual fun Mpv.getPropertyArray(name: String): List<MpvNode>? {
    return LibMpv.getPropertyArray(handle, name)?.asList()
}

public actual fun Mpv.getPropertyMap(name: String): Map<String, MpvNode>? {
    return LibMpv.getPropertyMap(handle, name)
}

public actual fun Mpv.getPropertyByteArray(name: String): ByteArray? {
    TODO()
}

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
    // val list = MpvNodeList().apply {
    //     num = value.size
    //     values = value.toTypedArray()
    // }
    //
    // LibMpv.setProperty(handle, name, MpvFormat.NODE_ARRAY.ordinal, list.pointer).checkError()

    TODO()
}

public actual fun Mpv.setProperty(name: String, value: Map<String, MpvNode>) {
    // val keys = value.keys.toTypedArray()
    // val values = value.values.toTypedArray()
    //
    // val map = MpvNodeMap().apply {
    //     num = value.size
    //     this.keys = keys
    //     this.values = values
    // }
    //
    // LibMpv.setProperty(handle, name, MpvFormat.NODE_MAP.ordinal, map.pointer).checkError()

    TODO()
}

public actual fun Mpv.setProperty(name: String, value: ByteArray) {
    // LibMpv.setProperty(handle, name, MpvFormat.BYTE_ARRAY.ordinal, mem.getPointer(0))
    TODO()
}

public actual fun <T> Mpv.observeProperty(name: String, callback: (T) -> Unit) {
    // LibMpv.observeProperty(handle, 0u, name, MpvFormat.NONE.ordinal).checkError()
    TODO()
}

public actual fun Mpv.unobserveProperty(name: String) {
    LibMpv.unobserveProperty(handle, 0)
}

public actual fun Mpv.delProperty(name: String) {
    LibMpv.delProperty(handle, name)
}