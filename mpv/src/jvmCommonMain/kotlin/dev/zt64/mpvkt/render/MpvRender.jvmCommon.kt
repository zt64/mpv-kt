package dev.zt64.mpvkt.render

import dev.zt64.mpvkt.LibMpv
import dev.zt64.mpvkt.Mpv

public actual fun Mpv.renderContextCreate(params: List<MpvRenderParam>): MpvRenderContext {
    return LibMpv.renderContextCreate(handle, params)
}