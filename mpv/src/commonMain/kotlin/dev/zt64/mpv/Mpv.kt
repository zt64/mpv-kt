package dev.zt64.mpv

/**
 * MPV client API wrapper
 *
 */
public expect class Mpv public constructor() {
    public val clientName: String
    public val clientId: Long

    /**
     * Set the log level
     *
     * @param level
     */
    public fun requestLogMessages(level: String)

    /**
     * Initialize the mpv instance
     */
    public fun init()

    /**
     * See also: [list-of-input-commands](https://mpv.io/manual/stable/#list-of-input-commands)
     *
     * @param args
     */
    public fun command(vararg args: String)

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
     * Interrupt the current mpv_wait_event() call. This will wake up the thread
     * currently waiting in mpv_wait_event(). If no thread is waiting, the next
     * mpv_wait_event() call will return immediately (this is to avoid lost
     * wakeups).
     *
     * mpv_wait_event() will receive an MPV_EVENT_NONE if it's woken up due to
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
    public fun setWakeupCallback(callback: () -> Unit)

    /**
     * Wait for all pending asynchronous requests to complete
     */
    public fun waitAsyncRequests()

    /**
     * TODO: Document
     *
     * @param reply
     * @param name
     * @param priority
     */
    public fun hookAdd(
        reply: Long,
        name: String,
        priority: Int
    )

    /**
     * TODO: Document
     *
     * @param id
     * @return
     */
    public fun hookContinue(id: Long): Int

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
     * Close the mpv instance
     */
    public fun close()

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