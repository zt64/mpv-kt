package dev.zt64.mpvkt

public actual class MpvStreamCbInfo

public actual inline fun <T : Any> MpvStreamCbInfo(
    cookie: T,
    read: MpvStreamCbReadFn<T>,
    seek: MpvStreamCbSeekFn<T>,
    size: MpvStreamCbSizeFn<T>,
    close: MpvStreamCbCloseFn<T>,
    cancel: MpvStreamCbCancelFn<T>
): MpvStreamCbInfo {
    return MpvStreamCbInfo()
}

public actual fun Mpv.streamCbAddRo(protocol: String, openFn: MpvStreamCbOpenRoFn) {
}