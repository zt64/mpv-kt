package dev.zt64.mpvkt.render

import cnames.structs.mpv_render_context
import dev.zt64.mpvkt.checkError
import kotlinx.cinterop.*
import mpv.*

public typealias MpvRenderContextHandle = CPointer<mpv_render_context>

@OptIn(ExperimentalStdlibApi::class)
public actual class MpvRenderContext(private val ctx: MpvRenderContextHandle) : AutoCloseable {
    public actual fun getInfo(param: MpvRenderParam) {
        mpv_render_context_get_info(ctx, param.readValue())
    }

    public actual fun setParameter(param: MpvRenderParam) {
        mpv_render_context_set_parameter(ctx, param.readValue())
    }

    public actual fun update(): ULong = mpv_render_context_update(ctx)

    public actual fun render(params: List<MpvRenderParam>) {
        memScoped {
            val arr =
                allocArrayOfPointersTo(
                    params.map {
                        alloc<MpvRenderParam> {
                            // type = it.type.ordinal.toUInt()
                            // data = it.data
                        }
                    }
                )

            mpv_render_context_render(ctx, arr.reinterpret()).checkError()
        }
    }

    public actual fun setUpdateCallback(callback: () -> Unit) {
        val ref = StableRef.create(callback)
        mpv_render_context_set_update_callback(
            ctx = ctx,
            callback = ref.asCPointer().reinterpret(),
            callback_ctx = null
        )
        ref.dispose()
    }

    actual override fun close(): Unit = mpv_render_context_free(ctx)
}