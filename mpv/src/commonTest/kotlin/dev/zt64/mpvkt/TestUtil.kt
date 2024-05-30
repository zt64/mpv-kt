package dev.zt64.mpvkt

fun testMpv(block: Mpv.() -> Unit) {
    val mpv = Mpv()

    mpv.block()

    mpv.close()
}