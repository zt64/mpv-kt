package dev.zt64.mpv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val URL = "https://filebin.net/zy07h0yezffz7ixc/microsoft_truck.mp4"

class MpvTest {
    @Test
    fun test() {
        // val mpv = Mpv()
        //
        // mpv.init()
        // mpv.requestLogMessages("v")
        //
        // mpv.setProperty("terminal", "yes")
        // mpv.setProperty("msg-level", "all=v")
        //
        // mpv.setWakeupCallback {
        //     println("Wakeup")
        // }
        //
        // mpv.command("loadfile", URL)
        //
        // while (true) {
        //     val event = mpv.waitEvent(1000)
        //
        //     // handle close event
        //     if (event.id == 1) break
        // }
        //
        // mpv.close()
    }

    @Test
    fun testVersion() {
        assertTrue { Mpv.clientApiVersion() > 0u }
    }

    @Test
    fun testErrorString() {
        assertEquals("success", Mpv.errorString(0))
    }

    @Test
    fun testProperty() {
        val mpv = Mpv()

        // Necessary for properties to work
        mpv.init()

        // Debugging
        mpv.setProperty("terminal", "yes")
        mpv.setProperty("msg-level", "all=v")

        // Test long property
        mpv.setProperty("volume", 50L)

        assertEquals(50L, mpv.getProperty<Long>("volume"))

        // Test string property
        mpv.setProperty("title", "Test Title")

        assertEquals("Test Title", mpv.getProperty<String>("title"))

        // Test boolean property
        mpv.setProperty("pause", true)

        assertEquals(true, mpv.getProperty<Boolean>("pause"))

        // Test double property
        mpv.setProperty("speed", 1.5)

        assertEquals(1.5, mpv.getProperty<Double>("speed"))

        // Test node property
        // val node = mpv.getPropertyNode("track-list")

        // println(node)
        // assertEquals(MpvFormat.NODE, node.format)

        mpv.close()
    }
}