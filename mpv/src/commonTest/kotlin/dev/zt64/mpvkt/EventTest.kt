package dev.zt64.mpvkt

import kotlin.test.Test

class EventTest {
    @Test
    fun testEvents() {
        val mpv = Mpv()

        mpv.observeProperty<Long>("volume") {
            println("Volume changed to $it")
        }

        mpv.close()
    }
}