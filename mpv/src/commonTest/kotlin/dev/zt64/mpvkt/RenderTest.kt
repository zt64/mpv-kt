package dev.zt64.mpvkt

import dev.zt64.mpvkt.render.MpvRenderApiType
import dev.zt64.mpvkt.render.renderContextCreate
import kotlin.test.Test

class RenderTest {
    @Test
    fun test() = runMpvTest { mpv ->
        mpv.renderContextCreate(MpvRenderApiType.OPENGL)

        // renderCtx.render()
    }
}