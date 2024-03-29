package dev.zt64.mpv

import com.sun.jna.Pointer

public actual fun Mpv.renderContextCreate(params: List<MpvRenderParam>): MpvRenderContext {
    val ctx = LibMpv.mpv_render_context()

    LibMpv.mpv_render_context_create(ctx, handle, Pointer.NULL)

    return MpvRenderContext(ctx)
}