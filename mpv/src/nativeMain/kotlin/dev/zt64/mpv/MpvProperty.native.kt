package dev.zt64.mpv

import kotlinx.cinterop.*
import mpv.*

public actual fun Mpv.getPropertyString(name: String): String? {
    return mpv_get_property_string(handle, name)!!.toKString()
}

public actual fun Mpv.getPropertyFlag(name: String): Boolean? {
    return memScoped {
        val result = alloc<IntVar>()
        mpv_get_property(handle, name, MPV_FORMAT_FLAG, result.ptr).checkError()
        result.value == 1
    }
}

public actual fun Mpv.getPropertyLong(name: String): Long? {
    return memScoped {
        val result = alloc<LongVar>()
        mpv_get_property(handle, name, MPV_FORMAT_INT64, result.ptr).checkError()
        result.value
    }
}

public actual fun Mpv.getPropertyDouble(name: String): Double? {
    return memScoped {
        val result = alloc<DoubleVar>()
        mpv_get_property(handle, name, MPV_FORMAT_DOUBLE, result.ptr).checkError()
        result.value
    }
}

public actual fun Mpv.getPropertyNode(name: String): MpvNode? {
    return memScoped {
        val result = alloc<MpvNode>()
        mpv_get_property(handle, name, MPV_FORMAT_NODE, result.ptr).checkError()
        result
    }
}

public actual fun Mpv.getPropertyNodeArray(name: String): List<MpvNode>? = TODO()

public actual fun Mpv.getPropertyNodeMap(name: String): Map<String, MpvNode>? = TODO()

public actual fun Mpv.getPropertyByteArray(name: String): ByteArray? = TODO()

public actual fun Mpv.setProperty(name: String, value: String) {
    mpv_set_property_string(handle, name, value).checkError()
}

public actual fun Mpv.setProperty(name: String, value: Boolean) {
    memScoped {
        val data = alloc<UInt>(if (value) 1u else 0u)

        mpv_set_property(handle, name, MPV_FORMAT_FLAG, data.ptr).checkError()
    }
}

public actual fun Mpv.setProperty(name: String, value: Long) {
    memScoped {
        val data = alloc(value)

        mpv_set_property(handle, name, MPV_FORMAT_INT64, data.ptr).checkError()
    }
}

public actual fun Mpv.setProperty(name: String, value: Double) {
    memScoped {
        val data = alloc(value)

        mpv_set_property(handle, name, MPV_FORMAT_DOUBLE, data.ptr).checkError()
    }
}

public actual fun Mpv.setProperty(name: String, value: MpvNode): Unit = TODO()

public actual fun Mpv.setProperty(name: String, value: List<MpvNode>): Unit = TODO()

public actual fun Mpv.setProperty(name: String, value: Map<String, MpvNode>): Unit = TODO()

public actual fun Mpv.setProperty(name: String, value: ByteArray): Unit = TODO()

public actual fun Mpv.observeProperty(name: String) {
    mpv_observe_property(handle, 0u, name, MPV_FORMAT_NONE).checkError()
    TODO()
}

public actual fun Mpv.unobserveProperty(name: String) {
    mpv_unobserve_property(handle, 0u).checkError()
    TODO()
}

public actual fun Mpv.delProperty(name: String) {
    mpv_del_property(handle, name).checkError()
}