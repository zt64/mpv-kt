package dev.zt64.mpvkt

/**
 * MPV client API wrapper
 *
 */
public expect class Mpv() : AutoCloseable {
    /**
     * The unique client name of this handle
     */
    public val clientName: String

    /**
     * The unique client id of this handle
     *
     * Never zero or negative
     */
    public val clientId: Long

    internal var isInitialized: Boolean

    /**
     * Set the log level
     *
     * @param level
     */
    public fun requestLogMessages(level: MpvLogLevel)

    /**
     * Initialize the mpv instance
     */
    public fun init()

    /**
     * Enable or disable an event
     *
     * @param eventId The event id
     * @param enable
     */
    public fun requestEvent(eventId: Int, enable: Boolean)

    /**
     * Wait for an event
     *
     * @param timeout The timeout in milliseconds
     * @return The event
     */
    public fun waitEvent(timeout: Long): MpvEvent

    /**
     * Interrupt the current [waitEvent] call. This will wake up the thread
     * currently waiting in [waitEvent]. If no thread is waiting, the next
     * [waitEvent] call will return immediately (this is to avoid lost
     * wakeups).
     *
     * [waitEvent] will receive an MPV_EVENT_NONE if it's woken up due to
     * this call. But note that this dummy event might be skipped if there are
     * already other events queued. All what counts is that the waiting thread
     * is woken up at all.
     *
     * Safe to be called from mpv render API threads.
     */
    public fun wakeup()

    /**
     * Set the wakeup callback
     *
     * @param callback
     */
    public fun setWakeupCallback(callback: MpvWakeupCallback)

    /**
     * Wait for all pending asynchronous requests to complete
     */
    public fun waitAsyncRequests()

    /**
     * TODO: Document
     *
     * @param name
     * @param priority
     */
    public fun addHook(name: String, priority: Int = 0, callback: () -> Unit)

    /**
     * TODO: Document
     *
     * @param id
     * @return
     */
    public fun hookContinue(id: Long)

    /**
     * Load a config file
     * See also: [Configuration Files](https://mpv.io/manual/stable/#configuration-files)
     *
     * @param path Absolute path to the config file
     */
    public fun loadConfigFile(path: String)

    /**
     * @return The internal time in nanoseconds
     * @see [getTimeUs]
     */
    public fun getTimeNs(): Long

    /**
     * @return The internal time in microseconds
     * @see [getTimeNs]
     */
    public fun getTimeUs(): Long

    /**
     * Close this instance
     */
    public override fun close()

    public companion object {
        /**
         * Get the mpv client API version
         *
         * @return The mpv client API version
         */
        public fun clientApiVersion(): ULong

        /**
         * Get an error string from an error code
         *
         * @param error
         * @return The error string
         */
        public fun errorString(error: Int): String
    }
}