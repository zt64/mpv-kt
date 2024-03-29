package dev.zt64.mpv

public actual typealias MpvRenderParam = LibMpv.mpv_render_param

public actual fun MpvRenderParam(name: String, value: String): MpvRenderParam {
    return MpvRenderParam().apply {
    }
}