package dev.zt64.mpvkt

@PublishedApi
internal inline fun Int.checkError() {
    if (this != MpvError.SUCCESS) throw MpvException(this)
}