package dev.zt64.mpvkt

import kotlin.test.Test

class HookTest {
    @Test
    fun testAddHook() = runMpvTest { mpv ->
        mpv.addHook("test-hook") {
            println("Hook called")
        }
    }
}