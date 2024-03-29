package dev.zt64.mpv

import com.sun.jna.Memory
import com.sun.jna.ptr.DoubleByReference
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.LongByReference

public actual fun Mpv.getPropertyString(name: String): String? =
    LibMpv.mpv_get_property_string(handle, name)

public actual fun Mpv.getPropertyFlag(name: String): Boolean? {
    val value = IntByReference()
    LibMpv.mpv_get_property(handle, name, MpvFormat.FLAG.ordinal, value.pointer).checkError()
    return value.value != 0
}

public actual fun Mpv.getPropertyLong(name: String): Long? {
    val ref = LongByReference()
    LibMpv.mpv_get_property(handle, name, MpvFormat.INT64.ordinal, ref.pointer).checkError()
    return ref.value
}

public actual fun Mpv.getPropertyDouble(name: String): Double? {
    val ref = DoubleByReference()
    LibMpv.mpv_get_property(handle, name, MpvFormat.DOUBLE.ordinal, ref.pointer).checkError()
    return ref.value
}

public actual fun Mpv.getPropertyNode(name: String): MpvNode? {
    val node = MpvNode()
    LibMpv.mpv_get_property(handle, name, MpvFormat.NODE.ordinal, node.pointer).checkError()
    return node
}

public actual fun Mpv.getPropertyNodeArray(name: String): List<MpvNode>? {
    val arr = MpvNodeList()
    LibMpv.mpv_get_property(handle, name, MpvFormat.NODE_ARRAY.ordinal, arr.pointer).checkError()
    return arr.values.asList()
}

public actual fun Mpv.getPropertyNodeMap(name: String): Map<String, MpvNode>? {
    val map = MpvNodeMap()
    LibMpv.mpv_get_property(handle, name, MpvFormat.NODE_MAP.ordinal, map.pointer).checkError()
    return map.keys.zip(map.values).toMap()
}

public actual fun Mpv.getPropertyByteArray(name: String): ByteArray? {
    val p = Memory(0)
    // LibMpv.mpv_get_property(handle, name, MpvFormat.BYTE_ARRAY, p)

    TODO()
}

public actual fun Mpv.setProperty(name: String, value: String) {
    LibMpv.mpv_set_property_string(handle, name, value).checkError()
}

public actual fun Mpv.setProperty(name: String, value: Boolean) {
    LibMpv
        .mpv_set_property(
            handle = handle,
            name = name,
            format = MpvFormat.FLAG.ordinal,
            data = IntByReference(if (value) 1 else 0).pointer
        ).checkError()
}

public actual fun Mpv.setProperty(name: String, value: Long) {
    LibMpv
        .mpv_set_property(handle, name, MpvFormat.INT64.ordinal, LongByReference(value).pointer)
        .checkError()
}

public actual fun Mpv.setProperty(name: String, value: Double) {
    LibMpv
        .mpv_set_property(handle, name, MpvFormat.DOUBLE.ordinal, DoubleByReference(value).pointer)
        .checkError()
}

public actual fun Mpv.setProperty(name: String, value: MpvNode) {
    LibMpv.mpv_set_property(handle, name, MpvFormat.NODE.ordinal, value.pointer).checkError()
}

public actual fun Mpv.setProperty(name: String, value: List<MpvNode>) {
    val list = MpvNodeList().apply {
        num = value.size
        values = value.toTypedArray()
    }

    LibMpv.mpv_set_property(handle, name, MpvFormat.NODE_ARRAY.ordinal, list.pointer).checkError()
}

public actual fun Mpv.setProperty(name: String, value: Map<String, MpvNode>) {
    val keys = value.keys.toTypedArray()
    val values = value.values.toTypedArray()

    val map = MpvNodeMap().apply {
        num = value.size
        this.keys = keys
        this.values = values
    }

    LibMpv.mpv_set_property(handle, name, MpvFormat.NODE_MAP.ordinal, map.pointer).checkError()
}

public actual fun Mpv.setProperty(name: String, value: ByteArray) {
    val mem = Memory(value.size.toLong())
    LibMpv.mpv_set_property(handle, name, MpvFormat.BYTE_ARRAY.ordinal, mem.getPointer(0))
}

public actual fun Mpv.observeProperty(name: String) {
    LibMpv.mpv_observe_property(handle, 0u, name, MpvFormat.NONE.ordinal).checkError()
    TODO()
}

public actual fun Mpv.unobserveProperty(name: String) {
    LibMpv.mpv_unobserve_property(handle, 0u).checkError()
}

public actual fun Mpv.delProperty(name: String) {
    LibMpv.mpv_del_property(handle, name).checkError()
}