package dev.zt64.mpvkt.render

import dev.zt64.mpvkt.LibMpv
import dev.zt64.mpvkt.MpvRenderUpdateCallback

public actual class MpvRenderContext  constructor(@JvmField public val type: MpvRenderApiType, private val ctx: Long) : AutoCloseable {
    public actual fun getInfo(param: RenderParam): Any? {
        TODO("Only next frame info is supported, so maybe this should replaced with a function that returns the next frame info")
        // return LibMpv.renderContextGetInfo(ctx, param)
    }

    public actual fun setParameter(param: RenderParam, value: Any) {
        LibMpv.renderContextSetParameter(ctx, param, value)
    }

    public actual fun update(): ULong {
        return LibMpv.renderContextUpdate(ctx).toULong()
    }

    public actual fun render(params: List<RenderParam>) {
        LibMpv.renderContextRender(ctx, this.type, params.toTypedArray())
    }

    public actual fun setUpdateCallback(callback: MpvRenderUpdateCallback) {
        LibMpv.renderContextSetUpdateCallback(ctx, callback)
    }

    public actual override fun close() {
        LibMpv.renderContextFree(ctx)
    }
}