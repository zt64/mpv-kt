package dev.zt64.mpvkt.render

import dev.zt64.mpvkt.LibMpv
import dev.zt64.mpvkt.MpvRenderUpdateCallback

public actual class MpvRenderContext internal constructor(private val ctx: Long) : AutoCloseable {
    public actual fun getInfo(param: MpvRenderParam): Any? {
        TODO("Only next frame info is supported, so maybe this should replaced with a function that returns the next frame info")
        // return LibMpv.renderContextGetInfo(ctx, param)
    }

    public actual fun setParameter(param: MpvRenderParam, value: Any) {
        LibMpv.renderContextSetParameter(ctx, param, value)
    }

    public actual fun update(): ULong {
        return LibMpv.renderContextUpdate(ctx)
    }

    public actual fun render(params: List<MpvRenderParam>) {
        LibMpv.renderContextRender(ctx, emptyArray())
    }

    public actual fun setUpdateCallback(callback: MpvRenderUpdateCallback) {
        LibMpv.renderContextSetUpdateCallback(ctx, callback)
    }

    public actual override fun close() {
        LibMpv.renderContextFree(ctx)
    }
}