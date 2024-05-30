package dev.zt64.mpvkt.render

import dev.zt64.mpvkt.LibMpv

public actual class MpvRenderContext internal constructor(private val ctx: MpvRenderContext) :
    AutoCloseable {
        public actual fun getInfo(param: MpvRenderParam) {
            LibMpv.renderContextGetInfo(ctx, param)
        }

        public actual fun setParameter(param: MpvRenderParam) {
            LibMpv.renderContextSetParameter(ctx, param)
        }

        public actual fun update(): ULong {
            return LibMpv.renderContextUpdate(ctx)
        }

        public actual fun render(params: List<MpvRenderParam>) {
            LibMpv.renderContextRender(ctx, emptyArray())
        }

        public actual fun setUpdateCallback(callback: () -> Unit) {
            LibMpv.renderContextSetUpdateCallback(ctx, callback)
        }

        public actual override fun close() {
            LibMpv.renderContextFree(ctx)
        }
    }