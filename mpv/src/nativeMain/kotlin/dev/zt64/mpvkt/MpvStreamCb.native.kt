package dev.zt64.mpvkt

import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.staticCFunction
import mpv.mpv_stream_cb_add_ro
import mpv.mpv_stream_cb_info

public actual typealias MpvStreamCbInfo = mpv_stream_cb_info

public actual inline fun <reified T : Any> MpvStreamCbInfo(
    cookie: T,
    crossinline read: MpvStreamCbReadFn<T>,
    crossinline seek: MpvStreamCbSeekFn<T>,
    crossinline size: MpvStreamCbSizeFn<T>,
    crossinline close: MpvStreamCbCloseFn<T>,
    crossinline cancel: MpvStreamCbCancelFn<T>
): MpvStreamCbInfo {
    return nativeHeap.alloc<MpvStreamCbInfo> {
        // this.cookie = StableRef.create(cookie).asCPointer()
        // this.read_fn = staticCFunction { cookie, buf, nbytes ->
        //     read(cookie!!.asStableRef<T>().get(), byteArrayOf(), nbytes)
        // }
        // this.seek_fn = staticCFunction { cookie, offset ->
        //     seek(cookie!!.asStableRef<T>().get(), offset)
        // }
        // this.size_fn = staticCFunction { cookie -> size(cookie!!.asStableRef<T>().get()) }
        // this.close_fn = staticCFunction { cookie -> close(cookie!!.asStableRef<T>().get()) }
        // this.cancel_fn = staticCFunction { cookie -> cancel(cookie!!.asStableRef<T>().get()) }
    }
}

public actual fun Mpv.streamCbAddRo(protocol: String, openFn: MpvStreamCbOpenRoFn) {
    mpv_stream_cb_add_ro(
        ctx = handle,
        protocol = protocol,
        user_data = null,
        open_fn = staticCFunction { userData, uri, info ->
            0
        }
    ).checkError()
}