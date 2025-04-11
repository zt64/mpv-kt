package dev.zt64.mpvkt.render

import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import mpv.mpv_render_param

public actual typealias MpvRenderParam = mpv_render_param

public actual fun MpvRenderParam(
    name: String,
    value: String
): MpvRenderParam {
    return nativeHeap.alloc<MpvRenderParam> {
        this.type
        this.data
    }
}