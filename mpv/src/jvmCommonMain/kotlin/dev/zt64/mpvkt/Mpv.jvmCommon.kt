package dev.zt64.mpvkt

public typealias MpvHandle = Long

public actual class Mpv(internal val handle: MpvHandle) : AutoCloseable {
    public actual constructor() : this(LibMpv.create())

    public actual val clientName: String
        get() = LibMpv.clientName(handle)
    public actual val clientId: Long
        get() = LibMpv.clientId(handle)

    internal actual var isInitialized: Boolean = false

    public actual fun requestLogMessages(level: MpvLogLevel) {
        LibMpv.requestLogMessages(handle, level.value.toString())
    }

    public actual fun init() {
        if (isInitialized) throw IllegalStateException("Already initialized")

        LibMpv.init(handle)

        isInitialized = true
    }

    public actual fun requestEvent(eventId: Int, enable: Boolean) {
        LibMpv.requestEvent(handle, eventId, enable)
    }

    public actual fun waitEvent(timeout: Long): MpvEvent {
        LibMpv.waitEvent(handle, timeout.toDouble())
        TODO()
    }

    public actual fun wakeup() {
        LibMpv.wakeup(handle)
    }

    public actual fun setWakeupCallback(callback: MpvWakeupCallback) {
        LibMpv.setWakeupCallback(handle, callback)
    }

    public actual fun waitAsyncRequests() {
        LibMpv.waitAsyncRequests(handle)
    }

    public actual fun addHook(name: String, priority: Int, callback: () -> Unit) {
        LibMpv.hookAdd(handle, 0, name, priority)
    }

    public actual fun hookContinue(id: Long) {
        LibMpv.hookContinue(handle, id)
    }

    public actual fun loadConfigFile(path: String) {
        LibMpv.loadConfigFile(handle, path)
    }

    public actual fun getTimeNs(): Long = LibMpv.getTimeNs(handle)

    public actual fun getTimeUs(): Long = LibMpv.getTimeUs(handle)

    public actual override fun close(): Unit = LibMpv.destroy(handle)

    public actual companion object {
        public actual fun clientApiVersion(): ULong = LibMpv.clientApiVersion().toULong()

        public actual fun errorString(error: Int): String = LibMpv.getErrorString(error)
    }
}