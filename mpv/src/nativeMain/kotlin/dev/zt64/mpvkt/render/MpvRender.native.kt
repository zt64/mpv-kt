package dev.zt64.mpvkt.render

import dev.zt64.mpvkt.Mpv
import kotlinx.cinterop.*
import mpv.mpv_render_context_create
import mpv.mpv_render_param

public actual fun Mpv.renderContextCreate(
    apiType: MpvRenderApiType,
    params: List<MpvRenderParam>
): MpvRenderContext {
    val res = nativeHeap.allocPointerTo<cnames.structs.mpv_render_context>()

    memScoped {
        mpv_render_context_create(
            res.ptr,
            handle,
            allocArrayOfPointersTo<mpv_render_param>().reinterpret()
        )
    }

    return MpvRenderContext(res.value!!)
}