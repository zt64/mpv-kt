package dev.zt64.mpvkt.render

import dev.zt64.mpvkt.Mpv

/**
 * Create a new render context.
 *
 * @param params
 * @return
 */
public expect fun Mpv.renderContextCreate(
    apiType: MpvRenderApiType,
    params: List<RenderParam> = emptyList()
): MpvRenderContext

public fun Mpv.renderContextCreate(
    apiType: MpvRenderApiType,
    vararg params: RenderParam
): MpvRenderContext {
    return renderContextCreate(apiType, params.toList())
}