package dev.zt64.mpvkt

public expect class MpvStreamCbInfo

/**
 * Create a new MpvStreamCbInfo instance.
 *
 * @param T The type of the cookie
 * @param cookie The cookie
 * @param read The read callback
 * @param seek The seek callback
 * @param size The size callback
 * @param close The close callback
 * @param cancel The cancel callback
 * @return A new MpvStreamCbInfo instance
 */
public expect inline fun <reified T : Any> MpvStreamCbInfo(
    cookie: T,
    crossinline read: MpvStreamCbReadFn<T>,
    crossinline seek: MpvStreamCbSeekFn<T>,
    crossinline size: MpvStreamCbSizeFn<T>,
    crossinline close: MpvStreamCbCloseFn<T>,
    crossinline cancel: MpvStreamCbCancelFn<T> = { _ -> }
): MpvStreamCbInfo

/**
 * Open callback used to implement a custom stream. The callback must return a
 * MpvStreamCbInfo instance, which contains the callbacks used to implement the
 * stream.
 */
public typealias MpvStreamCbOpenRoFn = (uri: String) -> MpvStreamCbInfo

/**
 * Read callback used to implement a custom stream. The semantics of the
 * callback match read(2) in blocking mode. Short reads are allowed (you can
 * return fewer bytes than requested, and libmpv will retry reading the rest
 * with another call). If no data can be immediately read, the callback must
 * block until there is new data. A return of 0 will be interpreted as final
 * EOF, although libmpv might retry the read, or seek to a different position.
 */
public typealias MpvStreamCbReadFn<T> = (cookie: T, buffer: ByteArray, nBytes: ULong) -> Long

/**
 * Seek callback used to implement a custom stream.
 *
 * Note that mpv will issue a seek to position 0 immediately after opening. This
 * is used to test whether the stream is seekable (since seekability might
 * depend on the URI contents, not just the protocol). Return
 * MPV_ERROR_UNSUPPORTED if seeking is not implemented for this stream. This
 * seek also serves to establish the fact that streams start at position 0.
 *
 * This callback can be NULL, in which it behaves as if always returning
 * MPV_ERROR_UNSUPPORTED.
 */
public typealias MpvStreamCbSeekFn<T> = (cookie: T, offset: Long) -> Long

/**
 * Size callback used to implement a custom stream.
 *
 * Return MPV_ERROR_UNSUPPORTED if no size is known.
 *
 * This callback can be NULL, in which it behaves as if always returning
 * MPV_ERROR_UNSUPPORTED.
 */
public typealias MpvStreamCbSizeFn<T> = (cookie: T) -> Long

/**
 * Close callback used to implement a custom stream.
 */
public typealias MpvStreamCbCloseFn<T> = (cookie: T) -> Unit

/**
 * Cancel callback used to implement a custom stream.
 *
 * This callback is used to interrupt any current or future read and seek
 * operations. It will be called from a separate thread than the demux
 * thread, and should not block.
 *
 * This callback can be NULL.
 *
 * Available since API 1.106.
 */
public typealias MpvStreamCbCancelFn<T> = (cookie: T) -> Unit

// public typealias MpvStreamCbReadFn = (data: ByteArray, size: Int) -> Int

/**
 * Add a custom stream protocol. This will register a protocol handler under
 * the given protocol prefix, and invoke the given callbacks if an URI with the
 * matching protocol prefix is opened.
 *
 * The "ro" is for read-only - only read-only streams can be registered with
 * this function.
 *
 * The callback remains registered until the mpv core is registered.
 *
 * If a custom stream with the same name is already registered, then the
 * MPV_ERROR_INVALID_PARAMETER error is returned.
 *
 * @param protocol
 * @param openFn
 */
public expect fun Mpv.streamCbAddRo(protocol: String, openFn: MpvStreamCbOpenRoFn)