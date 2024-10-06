package dev.zt64.mpvkt

import kotlin.test.Test
import kotlin.test.assertTrue

class EventTest {
    @Test
    fun testEvents() = runMpvTest { mpv ->
        var observed = false

        mpv.observeProperty<Long>("volume") {
            println("Volume changed to $it")
            observed = true
        }

        mpv.setProperty("volume", 50L)

        assertTrue(observed, "Event was not observed")
    }
}