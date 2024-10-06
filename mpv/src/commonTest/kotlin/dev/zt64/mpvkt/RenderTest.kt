package dev.zt64.mpvkt

import dev.zt64.mpvkt.render.renderContextCreate
import kotlin.test.Test

class RenderTest {
    @Test
    fun test() = runMpvTest { mpv ->
        val renderCtx = mpv.renderContextCreate()

        // renderCtx.render()
    }
}