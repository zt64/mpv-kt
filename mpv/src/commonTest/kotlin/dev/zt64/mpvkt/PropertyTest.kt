package dev.zt64.mpvkt

import kotlin.test.Test
import kotlin.test.assertEquals

class PropertyTest {
    private val mpv = Mpv()

    @Test
    fun test() {
        // val mpv = Mpv()
        //
        // mpv.init()
        // mpv.requestLogMessages("v")
        //
        // mpv.setProperty("terminal", true)
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
    fun testProperty() {
        val mpv = Mpv()

        // Necessary for properties to work
        mpv.init()

        // Test long property
        mpv.setProperty("volume", 50L)
        assertEquals(50L, mpv.getProperty("volume"))

        // Test string property
        mpv.setProperty("title", "Test Title")
        assertEquals("Test Title", mpv.getProperty("title"))

        // Test boolean property
        mpv.setProperty("pause", true)
        assertEquals(true, mpv.getProperty("pause"))

        // Test double property
        mpv.setProperty("speed", 1.5)
        assertEquals(1.5, mpv.getProperty("speed"))

        mpv.close()
    }

    @Test
    fun testNodeMap() {
        val mpv = Mpv()

        val node = mpv.getPropertyNode("audio-params")!!

        val map = mapOf<String, MpvNode>()

        mpv.close()
    }

    @Test
    fun testNodeArray() {
        val mpv = Mpv()

        val node = mpv.getPropertyNodeArray("track-list")!!
    }
}