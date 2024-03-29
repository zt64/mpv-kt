package dev.zt64.mpv

import com.sun.jna.*

private typealias MpvHandle = Long

public actual class Mpv public actual constructor() : AutoCloseable {
    public val handle: MpvHandle

    public actual val clientName: String
        get() = LibMpv.mpv_client_name(handle)
    public actual val clientId: Long
        get() = LibMpv.mpv_client_id(handle)

    init {
        Libc.setlocale(Libc.Category.LC_NUMERIC, "C")
        handle = LibMpv.mpv_create()
    }

    public actual fun requestLogMessages(level: String) {
        LibMpv.mpv_request_log_messages(handle, level).checkError()
    }

    public actual fun init() {
        LibMpv.mpv_initialize(handle).checkError()
    }

    public actual fun command(vararg args: String) {
        LibMpv.mpv_command(handle, args).checkError()
    }

    public actual fun requestEvent(eventId: Int, enable: Boolean) {
        LibMpv.mpv_request_event(handle, eventId, if (enable) 1 else 0).checkError()
    }

    public actual fun waitEvent(timeout: Long): MpvEvent {
        return LibMpv.mpv_wait_event(handle, timeout.toDouble())!!
    }

    public actual fun wakeup() {
        LibMpv.mpv_wakeup(handle)
    }

    public actual fun setWakeupCallback(callback: () -> Unit) {
        LibMpv.mpv_set_wakeup_callback(handle, callback, null)
    }

    public actual fun waitAsyncRequests() {
        LibMpv.mpv_wait_async_requests(handle)
    }

    public actual fun hookAdd(
        reply: Long,
        name: String,
        priority: Int
    ) {
        LibMpv.mpv_hook_add(handle, reply, name, priority).checkError()
    }

    public actual fun hookContinue(id: Long): Int {
        return LibMpv.mpv_hook_continue(handle, id)
    }

    public actual fun loadConfigFile(path: String) {
        LibMpv.mpv_load_config_file(handle, path).checkError()
    }

    public actual fun getTimeNs(): Long = LibMpv.mpv_get_time_ns(handle)

    public actual fun getTimeUs(): Long = LibMpv.mpv_get_time_us(handle)

    public actual override fun close(): Unit = LibMpv.mpv_terminate_destroy(handle)

    public actual companion object {
        public actual fun clientApiVersion(): ULong = LibMpv.mpv_client_api_version().toULong()

        public actual fun errorString(error: Int): String = LibMpv.mpv_error_string(error)
    }
}

public object Libc : Library {
    public external fun setlocale(category: Int, locale: String?): String

    public object Category {
        public const val LC_NUMERIC: Int = 1
    }

    init {
        Native.register(Platform.C_LIBRARY_NAME)
    }
}

@Suppress("FunctionName", "ClassName", "PropertyName", "LocalVariableName")
public interface LibMpv : Library {
    public fun mpv_client_api_version(): Long

    public fun mpv_client_name(handle: MpvHandle): String

    public fun mpv_client_id(handle: MpvHandle): Long

    public fun mpv_error_string(error: Int): String

    public fun mpv_create(): MpvHandle

    public fun mpv_initialize(handle: MpvHandle): Int

    public fun mpv_terminate_destroy(handle: MpvHandle)

    public fun mpv_request_log_messages(handle: MpvHandle, min_level: String): Int

    public fun mpv_set_wakeup_callback(
        handle: MpvHandle,
        callback: mpv_wakeup_fn,
        callback_ctx: Pointer?
    ): Int

    public fun mpv_wait_async_requests(handle: MpvHandle)

    public fun mpv_hook_add(
        handle: MpvHandle,
        reply_userdata: Long,
        name: String,
        priority: Int
    ): Int

    public fun mpv_hook_continue(handle: MpvHandle, id: Long): Int

    public fun mpv_command(handle: MpvHandle, args: Array<out String>): Int

    public fun mpv_command_async(
        handle: MpvHandle,
        reply_userdata: Long,
        args: Array<out String>
    ): Int

    public fun mpv_command_string(handle: MpvHandle, args: String): Int

    public fun mpv_set_property(
        handle: MpvHandle,
        name: String,
        format: Int,
        data: Pointer
    ): Int

    public fun mpv_set_property_string(
        handle: MpvHandle,
        name: String,
        data: String
    ): Int

    public fun mpv_del_property(handle: MpvHandle, name: String): Int

    public fun mpv_set_property_async(
        handle: MpvHandle,
        reply_userdata: Long,
        name: String,
        format: Int,
        data: Pointer
    ): Int

    public fun mpv_get_property(
        handle: MpvHandle,
        name: String,
        format: Int,
        data: Pointer
    ): Int

    public fun mpv_get_property_string(handle: MpvHandle, name: String): String

    public fun mpv_get_property_osd_string(handle: MpvHandle, name: String): Pointer?

    public fun mpv_get_property_async(
        handle: MpvHandle,
        reply_userdata: Long,
        name: String,
        format: Int
    ): Int

    public fun mpv_observe_property(
        handle: MpvHandle,
        reply_userdata: ULong,
        name: String,
        format: Int
    ): Int

    public fun mpv_unobserve_property(handle: MpvHandle, reply_userdata: ULong): Int

    public fun mpv_wait_event(handle: MpvHandle, timeOut: Double): MpvEvent?

    public fun mpv_wakeup(handle: MpvHandle)

    public fun mpv_request_event(
        handle: MpvHandle,
        event_id: Int,
        enable: Int
    ): Int

    public fun mpv_free(data: Pointer)

    public fun mpv_render_context_create(
        pointer: mpv_render_context,
        handle: MpvHandle,
        params: Pointer
    ): Int

    public fun mpv_render_context_set_parameter(
        pointer: mpv_render_context,
        param: mpv_render_param
    ): Int

    public fun mpv_render_context_get_info(
        pointer: mpv_render_context,
        param: mpv_render_param
    ): Int

    public fun mpv_render_context_set_update_callback(
        pointer: mpv_render_context,
        callback: mpv_render_update_fn,
        callback_ctx: Pointer? = null
    )

    public fun mpv_render_context_update(pointer: mpv_render_context): ULong

    public fun mpv_render_context_render(
        pointer: mpv_render_context,
        params: Array<mpv_render_param>
    ): Int

    public fun mpv_render_context_free(pointer: mpv_render_context)

    public fun mpv_load_config_file(handle: MpvHandle, file: String): Int

    public fun mpv_get_time_ns(handle: MpvHandle): Long

    public fun mpv_get_time_us(handle: MpvHandle): Long

    @Structure.FieldOrder("data")
    public class mpv_render_context : Structure() {
        @JvmField
        public var data: Pointer? = null
    }

    @Structure.FieldOrder("type", "data")
    public open class mpv_render_param : Structure() {
        @JvmField
        public var type: Int = 0

        @JvmField
        public var data: Pointer? = null
    }

    @Structure.FieldOrder("get_proc_address", "get_proc_address_ctx")
    public class mpv_opengl_init_params : Structure() {
        @JvmField
        public var get_proc_address: mpv_proc_address_fn? = null

        @JvmField
        public var get_proc_address_ctx: Pointer? = null
    }

    @Structure.FieldOrder("fbo", "w", "h", "internal_format")
    public class mpv_opengl_fbo : Structure() {
        @JvmField
        public var fbo: Int = 0

        @JvmField
        public var w: Int = 0

        @JvmField
        public var h: Int = 0

        @JvmField
        public var internal_format: Int = 0
    }

    public fun interface mpv_wakeup_fn : Callback {
        public fun invoke()
    }

    public fun interface mpv_render_update_fn : Callback {
        public fun invoke()
    }

    public fun interface mpv_proc_address_fn : Callback {
        public fun invoke(ctx: Pointer, name: String): Pointer
    }

    public companion object : LibMpv by Native.load("mpv", LibMpv::class.java)
}

// public object LibMpv {
//     init {
//         System.loadLibrary("mpv")
//
//         NativeLoader.loadLibrary(Mpv::class.java.classLoader, "mpv-kt-jvm")
//     }
//
//     @JvmStatic
//     public external fun create(): MpvHandle
//
//     @JvmStatic
//     public external fun init(handle: MpvHandle): Int
//
//     @JvmStatic
//     public external fun setWakeupCallback(callback: WakeupCallback)
//
//     @JvmStatic
//     public external fun waitEvent(timeout: Int): MpvEvent
//
//     @JvmStatic
//     public external fun destroy(handle: Long)
//
//     @JvmStatic
//     public external fun attachSurface(surface: Long)
//
//     @JvmStatic
//     public external fun detachSurface()
//
//     @JvmStatic
//     public external fun command(cmd: Array<String?>): Int
//
//     @JvmStatic
//     public external fun commandAsync(cmd: Array<String?>): Int
//
//     @JvmStatic
//     public external fun setOptionString(name: String, value: String): Int
//
//     // public external fun grabThumbnail(dimension: Int): Bitmap?
//     @JvmStatic
//     public external fun observeProperty(property: String, format: Int)
//
//     @JvmStatic
//     public external fun renderContextCreate(handle: MpvHandle, params: List<MpvRenderParam>): Long
//
//     @JvmStatic
//     public external fun renderContextRender(handle: Long, params: Array<MpvRenderParam>): Int
//
//     @JvmStatic
//     public external fun renderContextSetUpdateCallback(handle: Long, callback: WakeupCallback)
//
//     @JvmStatic
//     public external fun renderContextUpdate(handle: Long): Int
//
//     @JvmStatic
//     public external fun renderContextFree(handle: Long)
//
//     private val observers = arrayListOf<EventObserver>()
//
//     @JvmStatic
//     public fun addObserver(o: EventObserver) {
//         synchronized(observers) { observers += o }
//     }
//
//     @JvmStatic
//     public fun removeObserver(o: EventObserver) {
//         synchronized(observers) { observers.remove(o) }
//     }
//
//     @JvmStatic
//     public fun eventProperty(property: String, value: Long) {
//         synchronized(observers) { for (o in observers) o.eventProperty(property, value) }
//     }
//
//     @JvmStatic
//     public fun eventProperty(property: String, value: Boolean) {
//         synchronized(observers) { for (o in observers) o.eventProperty(property, value) }
//     }
//
//     @JvmStatic
//     public fun eventProperty(property: String, value: String) {
//         synchronized(observers) { for (o in observers) o.eventProperty(property, value) }
//     }
//
//     @JvmStatic
//     public fun eventProperty(property: String) {
//         synchronized(observers) { for (o in observers) o.eventProperty(property) }
//     }
//
//     @JvmStatic
//     public fun event(eventId: Int) {
//         synchronized(observers) {
//             observers.forEach { it.event(eventId) }
//         }
//     }
//
//     private val log_observers: MutableList<LogObserver> = arrayListOf()
//
//     @JvmStatic
//     public fun addLogObserver(o: LogObserver) {
//         synchronized(log_observers) { log_observers += o }
//     }
//
//     @JvmStatic
//     public fun removeLogObserver(o: LogObserver) {
//         synchronized(log_observers) { log_observers.remove(o) }
//     }
//
//     @JvmStatic
//     public fun logMessage(prefix: String, level: Int, text: String) {
//         synchronized(log_observers) { for (o in log_observers) o.logMessage(prefix, level, text) }
//     }
//
//     public interface EventObserver {
//         public fun eventProperty(property: String)
//         public fun eventProperty(property: String, value: Long)
//         public fun eventProperty(property: String, value: Boolean)
//         public fun eventProperty(property: String, value: String)
//         public fun event(eventId: Int)
//     }
//
//     public interface LogObserver {
//         public fun logMessage(prefix: String, level: Int, text: String)
//     }
// }

public fun interface WakeupCallback {
    @JvmSynthetic
    public fun wakeup()
}