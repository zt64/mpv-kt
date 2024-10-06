package dev.zt64.mpvkt

import kotlinx.cinterop.toKString
import mpv.mpv_error_string

@PublishedApi
internal inline fun Int.checkError() {
    if (this != MpvError.SUCCESS) throw MpvException(mpv_error_string(this)!!.toKString())
}