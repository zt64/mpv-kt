package dev.zt64.mpv

@PublishedApi
internal inline fun Int.checkError() {
    if (this != MpvError.SUCCESS) throw MpvException(this)
}