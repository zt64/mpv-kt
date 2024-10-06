package dev.zt64.mpvkt

import cnames.structs.mpv_handle
import kotlinx.cinterop.*
import mpv.*

internal typealias MpvHandle = CPointer<mpv_handle>

public actual class Mpv public actual constructor() : AutoCloseable {
    @PublishedApi
    internal val handle: MpvHandle = try {
        mpv_create()!!
    } catch (e: Throwable) {
        error("Failed to create mpv handle: ${e.message}")
    }

    public actual val clientName: String
        get() = mpv_client_name(handle)?.toKString() ?: "Unknown client"
    public actual val clientId: Long
        get() = mpv_client_id(handle)

    public actual fun init() {
        mpv_initialize(handle).checkError()
    }

    public actual fun requestLogMessages(level: MpvLogLevel) {
        mpv_request_log_messages(handle, level.toString()).checkError()
    }

    public actual fun requestEvent(eventId: Int, enable: Boolean) {
        mpv_request_event(handle, eventId.toUInt(), if (enable) 1 else 0).checkError()
    }

    public actual fun waitEvent(timeout: Long): MpvEvent {
        mpv_wait_event(handle, timeout.toDouble())!!.pointed
        TODO()
    }

    public actual fun wakeup(): Unit = mpv_wakeup(handle)

    public actual fun setWakeupCallback(callback: () -> Unit) {
        val ref = StableRef.create(callback)

        mpv_set_wakeup_callback(
            ctx = handle,
            cb = ref.asCPointer().reinterpret(),
            d = null
        )

        ref.dispose()
    }

    public actual fun waitAsyncRequests(): Unit = mpv_wait_async_requests(handle)

    public actual fun addHook(
        reply: Long,
        name: String,
        priority: Int,
        callback: () -> Unit
    ) {
        mpv_hook_add(handle, reply.toULong(), name, priority).checkError()
    }

    public actual fun hookContinue(id: Long) {
        mpv_hook_continue(handle, id.toULong()).checkError()
    }

    public actual fun loadConfigFile(path: String) {
        mpv_load_config_file(handle, path).checkError()
    }

    public actual fun getTimeNs(): Long = mpv_get_time_ns(handle)

    public actual fun getTimeUs(): Long = mpv_get_time_us(handle)

    public actual override fun close(): Unit = mpv_terminate_destroy(handle)

    public actual companion object {
        public actual fun clientApiVersion(): ULong = mpv_client_api_version()

        public actual fun errorString(error: Int): String {
            return mpv_error_string(error)?.toKString() ?: "Unknown error"
        }
    }
}