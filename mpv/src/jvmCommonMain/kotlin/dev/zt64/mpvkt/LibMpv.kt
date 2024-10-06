package dev.zt64.mpvkt

import dev.zt64.mpvkt.render.MpvRenderContext
import dev.zt64.mpvkt.render.MpvRenderParam

internal object LibMpv {
    init {
        NativeLoader.loadLibrary("mpv_jni")
    }

    @JvmStatic
    external fun clientApiVersion(): Long

    @JvmStatic
    external fun create(): MpvHandle

    @JvmStatic
    external fun init(handle: MpvHandle)

    @JvmStatic
    external fun destroy(handle: MpvHandle)

    @JvmStatic
    external fun wakeup(handle: MpvHandle)

    @JvmStatic
    external fun requestLogMessages(handle: MpvHandle, level: String)

    @JvmStatic
    external fun waitAsyncRequests(handle: MpvHandle)

    @JvmStatic
    external fun command(handle: MpvHandle, cmd: Array<String>)

    @JvmStatic
    external fun command(handle: MpvHandle, node: MpvNode)

    @JvmStatic
    external fun commandRet(handle: MpvHandle, cmd: Array<String>)

    @JvmStatic
    external fun command(handle: MpvHandle, cmd: String)

    @JvmStatic
    external fun commandAsync(
        handle: MpvHandle,
        requestId: Long,
        cmd: Array<String>
    )

    @JvmStatic
    external fun commandAsync(
        handle: MpvHandle,
        requestId: Long,
        node: MpvNode
    )

    @JvmStatic
    external fun abortAsyncCommand(handle: MpvHandle, requestId: Long)

    @JvmStatic
    external fun clientName(handle: Long): String

    @JvmStatic
    external fun clientId(handle: Long): Long

    @JvmStatic
    external fun setOption(
        handle: MpvHandle,
        name: String,
        value: String
    ): Int

    @JvmName("setPropertyLong")
    external fun setProperty(
        handle: MpvHandle,
        property: String,
        value: Long
    )

    @JvmStatic
    external fun getPropertyLong(handle: MpvHandle, property: String): Long?

    @JvmName("setPropertyDouble")
    external fun setProperty(
        handle: MpvHandle,
        property: String,
        value: Double
    )

    @JvmStatic
    external fun getPropertyDouble(handle: MpvHandle, property: String): Double?

    @JvmName("setPropertyFlag")
    external fun setProperty(
        handle: MpvHandle,
        property: String,
        value: Boolean
    )

    @JvmStatic
    external fun getPropertyFlag(handle: MpvHandle, property: String): Boolean?

    @JvmName("setPropertyString")
    external fun setProperty(
        handle: MpvHandle,
        property: String,
        value: String
    )

    @JvmStatic
    external fun getPropertyString(handle: MpvHandle, property: String): String?

    @JvmName("setPropertyNode")
    external fun setProperty(
        handle: MpvHandle,
        property: String,
        value: MpvNode
    )

    @JvmStatic
    external fun getPropertyNode(handle: MpvHandle, property: String): MpvNode?

    @JvmStatic
    external fun getPropertyArray(handle: MpvHandle, property: String): Array<MpvNode>?

    @JvmStatic
    external fun getPropertyMap(handle: MpvHandle, property: String): Map<String, MpvNode>?

    @JvmStatic
    external fun observeProperty(
        handle: MpvHandle,
        property: String,
        format: Int
    )

    @JvmStatic
    external fun unobserveProperty(handle: MpvHandle, id: Long)

    @JvmStatic
    external fun delProperty(handle: MpvHandle, property: String)

    @JvmStatic
    external fun hookAdd(
        handle: MpvHandle,
        reply: Long,
        name: String,
        priority: Int
    )

    @JvmStatic
    external fun hookContinue(handle: MpvHandle, id: Long)

    @JvmStatic
    external fun getTimeNs(handle: MpvHandle): Long

    @JvmStatic
    external fun getTimeUs(handle: MpvHandle): Long

    @JvmStatic
    external fun requestEvent(
        handle: MpvHandle,
        event: Int,
        enable: Boolean
    ): Int

    @JvmStatic
    external fun waitEvent(handle: MpvHandle, timeout: Double): Int

    @JvmStatic
    external fun setWakeupCallback(handle: MpvHandle, callback: () -> Unit)

    @JvmStatic
    external fun getErrorString(error: Int): String

    @JvmStatic
    external fun loadConfigFile(handle: MpvHandle, path: String): Int

    @JvmStatic
    external fun renderContextCreate(
        handle: MpvHandle,
        params: List<MpvRenderParam>
    ): MpvRenderContext

    @JvmStatic
    external fun renderContextFree(ctx: MpvRenderContext)

    @JvmStatic
    external fun renderContextGetInfo(ctx: MpvRenderContext, param: MpvRenderParam)

    @JvmStatic
    external fun renderContextSetParameter(ctx: MpvRenderContext, param: MpvRenderParam)

    @JvmStatic
    external fun renderContextUpdate(ctx: MpvRenderContext): ULong

    @JvmStatic
    external fun renderContextRender(ctx: MpvRenderContext, params: Array<MpvRenderParam>)

    @JvmStatic
    external fun renderContextSetUpdateCallback(ctx: MpvRenderContext, callback: () -> Unit)
}