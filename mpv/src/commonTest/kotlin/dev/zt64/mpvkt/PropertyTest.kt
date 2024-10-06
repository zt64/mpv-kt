package dev.zt64.mpvkt

import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

const val SAMPLE_URL = "https://www.youtube.com/watch?v=6JYIGclVQdw"

class PropertyTest {
    @Test
    fun testProperty() = runMpvTest { mpv ->
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
    }

    @Test
    fun testNodeMap() = runMpvTest { mpv ->
        mpv.init()

        mpv.setOption("pause", "yes")
        mpv.command("loadfile", "/home/nick/Videos/8aed9a24726a48d2beb4391eef7d9957.mp4")

        delay(1000)

        val node = mpv.getPropertyMap("metadata")!!

        println(node.entries.joinToString())
    }

    @Test
    fun testNodeArray() = runMpvTest { mpv ->
        mpv.init()

        val node = mpv.getPropertyArray("property-list")!!

        println(node.joinToString())
    }

    @Test
    fun testException() = runMpvTest { mpv ->
        mpv.init()

        assertFails {
            mpv.getPropertyString("")
        }
    }
}