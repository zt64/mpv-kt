package dev.zt64.mpv

internal typealias MpvRenderContextHandle = LibMpv.mpv_render_context

public actual class MpvRenderContext internal constructor(private val ctx: MpvRenderContextHandle) :
    AutoCloseable {
        public actual fun getInfo(param: MpvRenderParam) {
            LibMpv.mpv_render_context_get_info(ctx, param)
        }

        public actual fun setParameter(param: MpvRenderParam) {
            LibMpv.mpv_render_context_set_parameter(ctx, param)
        }

        public actual fun update(): ULong {
            return LibMpv.mpv_render_context_update(ctx)
        }

        public actual fun render(params: List<MpvRenderParam>) {
            LibMpv.mpv_render_context_render(ctx, emptyArray()).checkError()
        }

        public actual fun setUpdateCallback(callback: () -> Unit) {
            LibMpv.mpv_render_context_set_update_callback(ctx, callback, null)
        }

        public actual override fun close() {
            LibMpv.mpv_render_context_free(ctx)
        }
    }