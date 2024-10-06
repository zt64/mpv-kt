package dev.zt64.mpvkt

import kotlinx.coroutines.runBlocking

/**
 * Setup MPV for testing and automatically close upon finishing
 *
 * @param block
 */
inline fun runMpvTest(crossinline block: suspend (mpv: Mpv) -> Unit) {
    runBlocking {
        val mpv = Mpv()

        mpv.setOption("terminal", "yes")
        mpv.setOption("msg-level", "all=v")

        block(mpv)

        mpv.close()
    }
}