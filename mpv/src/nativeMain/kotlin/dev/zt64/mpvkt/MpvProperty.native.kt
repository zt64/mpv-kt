package dev.zt64.mpvkt

import kotlinx.cinterop.*
import mpv.*

private inline fun <reified T : CVariable> MemScope.getPropertyCommon(handle: MpvHandle, name: String, format: UInt): T = alloc<T> {
    mpv_get_property(handle, name, format, ptr).checkError()
}

public actual fun Mpv.getPropertyString(name: String): String? {
    return mpv_get_property_string(handle, name)?.toKString()
}

public actual fun Mpv.getPropertyFlag(name: String): Boolean? = memScoped {
    getPropertyCommon<IntVar>(handle, name, MPV_FORMAT_FLAG).value == 1
}

public actual fun Mpv.getPropertyLong(name: String): Long? = memScoped {
    getPropertyCommon<LongVar>(handle, name, MPV_FORMAT_INT64).value
}

public actual fun Mpv.getPropertyDouble(name: String): Double? = memScoped {
    getPropertyCommon<DoubleVar>(handle, name, MPV_FORMAT_DOUBLE).value
}

public actual fun Mpv.getPropertyArray(name: String): List<MpvNode>? = memScoped {
    val result = getPropertyCommon<mpv_node_list>(handle, name, MPV_FORMAT_NODE_ARRAY)

    buildList {
        for (i in 0 until result.num) {
            add(result.values!![i].toKotlin())
        }
    }
}

public actual fun Mpv.getPropertyMap(name: String): Map<String, MpvNode>? = memScoped {
    getPropertyCommon<mpv_node_list>(handle, name, MPV_FORMAT_NODE_MAP)

    TODO()
}

public actual fun Mpv.getPropertyByteArray(name: String): ByteArray? = TODO()

public actual fun Mpv.setOption(name: String, value: String) {
    mpv_set_option_string(handle, name, value).checkError()
}

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

public actual fun Mpv.setProperty(name: String, value: List<MpvNode>): Unit = TODO()

public actual fun Mpv.setProperty(name: String, value: Map<String, MpvNode>): Unit = TODO()

public actual fun Mpv.setProperty(name: String, value: ByteArray): Unit = TODO()

public actual fun <T> Mpv.observeProperty(name: String, callback: (T) -> Unit) {
    mpv_observe_property(handle, 0u, name, MPV_FORMAT_NONE).checkError()
    TODO()
}

public actual fun Mpv.unobserveProperty(name: String) {
    mpv_unobserve_property(handle, 0u).checkError()
    TODO()
}

public actual fun Mpv.deleteProperty(name: String) {
    mpv_del_property(handle, name).checkError()
}