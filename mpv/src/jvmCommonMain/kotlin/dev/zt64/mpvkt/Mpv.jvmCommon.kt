package dev.zt64.mpvkt

public actual typealias MpvHandle = Long

public actual class Mpv public actual constructor(public val handle: MpvHandle) :
    AutoCloseable {
        public actual val clientName: String
            get() = LibMpv.clientName(handle)
        public actual val clientId: Long
            get() = LibMpv.clientId(handle)

        public actual fun requestLogMessages(level: MpvLogLevel) {
            LibMpv.requestLogMessages(handle, level.toString())
        }

        public actual fun init() {
            LibMpv.init(handle)
        }

        public actual fun requestEvent(eventId: Int, enable: Boolean) {
            LibMpv.requestEvent(handle, eventId, enable)
        }

        public actual fun waitEvent(timeout: Long): MpvEvent {
            // return LibMpv.waitEvent(handle, timeout.toDouble())!!
            TODO()
        }

        public actual fun wakeup() {
            LibMpv.wakeup(handle)
        }

        public actual fun setWakeupCallback(callback: () -> Unit) {
            LibMpv.setWakeupCallback(handle, callback)
        }

        public actual fun waitAsyncRequests() {
            LibMpv.waitAsyncRequests(handle)
        }

        public actual fun hookAdd(
            reply: Long,
            name: String,
            priority: Int,
            callback: () -> Unit
        ) {
            LibMpv.hookAdd(handle, reply, name, priority)
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

            public actual fun createHandle(): MpvHandle = LibMpv.create()
        }
    }