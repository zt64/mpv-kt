package dev.zt64.mpvkt.render

import dev.zt64.mpvkt.Mpv

/**
 * Create a new render context.
 *
 * @param params
 * @return
 */
public expect fun Mpv.renderContextCreate(
    params: List<MpvRenderParam> = emptyList()
): MpvRenderContext