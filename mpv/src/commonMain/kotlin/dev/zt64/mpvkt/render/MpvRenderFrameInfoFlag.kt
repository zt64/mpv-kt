package dev.zt64.mpvkt.render

public enum class MpvRenderFrameInfoFlag(public val value: Int) {
    MPV_RENDER_FRAME_INFO_PRESENT(1 shl 0),
    MPV_RENDER_FRAME_INFO_REDRAW(1 shl 1),
    MPV_RENDER_FRAME_INFO_REPEAT(1 shl 2),
    MPV_RENDER_FRAME_INFO_BLOCK_VSYNC(1 shl 3)
}