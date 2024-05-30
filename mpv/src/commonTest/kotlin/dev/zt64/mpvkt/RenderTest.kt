package dev.zt64.mpvkt

import dev.zt64.mpvkt.render.renderContextCreate
import kotlin.test.Test

class RenderTest {
    @Test
    fun test() = testMpv {
        val renderCtx = renderContextCreate()

        renderCtx.render()
    }
}