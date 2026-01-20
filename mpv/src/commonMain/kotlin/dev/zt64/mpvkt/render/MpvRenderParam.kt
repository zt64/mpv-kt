package dev.zt64.mpvkt.render

import dev.zt64.mpvkt.MpvNode
import kotlin.jvm.JvmField
import kotlin.jvm.JvmName

public sealed class RenderParam(
    @JvmField
    public val type: MpvRenderParamType,
    @JvmField
    public val data: Any?,
) {
    public companion object {
            public data class OpenGLInitParams(
                @JvmField
                val getProcAddress: Long,
                @JvmField
                val getProcAddressCtx: Long
            ) : RenderParam(MpvRenderParamType.OPENGL_INIT_PARAMS, 0L)

            public data class OpenGLFBO(
                @JvmField
                val fbo: Int,
                @JvmField
                val w: Int,
                @JvmField
                val h: Int,
                @JvmField
                val internalFormat: Int,
            ) : RenderParam(
                MpvRenderParamType.OPENGL_FBO,
                fbo.toLong()
            )

            public data class FlipY(@JvmField val flipY: Boolean) : RenderParam(
                MpvRenderParamType.FLIP_Y,
                flipY
            )

            public data class Depth(val depth: Int) : RenderParam(
                MpvRenderParamType.DEPTH,
                depth
            )

            public data class IccProfile(val profile: String) : RenderParam(
                MpvRenderParamType.ICC_PROFILE,
                profile
            )

            public data class AmbientLight(val light: Float) : RenderParam(
                MpvRenderParamType.AMBIENT_LIGHT,
                light
            )

            public data class X11Display(val display: String) : RenderParam(
                MpvRenderParamType.X11_DISPLAY,
                display
            )

            public data class WlDisplay(val display: String) : RenderParam(
                MpvRenderParamType.WL_DISPLAY,
                display
            )

            public data class AdvancedControl(val control: Boolean) : RenderParam(
                MpvRenderParamType.ADVANCED_CONTROL,
                if (control) 1L else 0L
            )

            public data class NextFrameInfo(val info: MpvNode) : RenderParam(
                MpvRenderParamType.NEXT_FRAME_INFO,
                info
            )

            public data class BlockForTargetTime(val block: Boolean) : RenderParam(
                MpvRenderParamType.BLOCK_FOR_TARGET_TIME,
                if (block) 1L else 0L
            )

            public data class SkipRendering(val skip: Boolean) : RenderParam(
                MpvRenderParamType.SKIP_RENDERING,
                if (skip) 1L else 0L
            )

            public data class DrmDisplay(val display: String) : RenderParam(
                MpvRenderParamType.DRM_DISPLAY,
                display
            )

            public data class DrmDrawSurfaceSize(val size: Int) : RenderParam(
                MpvRenderParamType.DRM_DRAW_SURFACE_SIZE,
                size.toLong()
            )

            public data class DrmDisplayV2(val display: String) : RenderParam(
                MpvRenderParamType.DRM_DISPLAY_V2,
                display
            )

            public data class SwSize(val size: Int) : RenderParam(
                MpvRenderParamType.SW_SIZE,
                size.toLong()
            )

            public data class SwFormat(val format: Int) : RenderParam(
                MpvRenderParamType.SW_FORMAT,
                format.toLong()
            )

            public data class SwStride(val stride: Int) : RenderParam(
                MpvRenderParamType.SW_STRIDE,
                stride.toLong()
            )

            public data class SwPointer(val pointer: Long) : RenderParam(
                MpvRenderParamType.SW_POINTER,
                pointer
            )
        }
}