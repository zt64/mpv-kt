package dev.zt64.mpvkt.render

import dev.zt64.mpvkt.LibMpv
import dev.zt64.mpvkt.Mpv

public actual fun Mpv.renderContextCreate(
    apiType: MpvRenderApiType,
    params: List<RenderParam>
): MpvRenderContext {
    return MpvRenderContext(LibMpv.renderContextCreate(handle, apiType, listOf()))
}