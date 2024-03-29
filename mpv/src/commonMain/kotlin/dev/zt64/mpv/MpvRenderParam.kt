package dev.zt64.mpv

public expect class MpvRenderParam

public expect fun MpvRenderParam(name: String, value: String): MpvRenderParam

public sealed interface RenderParam {
    public val type: MpvRenderParamType

    public data object Invalid : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.INVALID
    }

    public data class ApiType(val apiType: String) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.API_TYPE
    }

    public data class OpenGLInitParams(val params: MpvNode) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.OPENGL_INIT_PARAMS
    }

    public data class OpenGLFBO(val fbo: Int) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.OPENGL_FBO
    }

    public data class FlipY(val flipY: Boolean) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.FLIP_Y
    }

    public data class Depth(val depth: Boolean) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.DEPTH
    }

    public data class IccProfile(val profile: String) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.ICC_PROFILE
    }

    public data class AmbientLight(val light: Float) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.AMBIENT_LIGHT
    }

    public data class X11Display(val display: String) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.X11_DISPLAY
    }

    public data class WlDisplay(val display: String) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.WL_DISPLAY
    }

    public data class AdvancedControl(val control: MpvNode) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.ADVANCED_CONTROL
    }

    public data class NextFrameInfo(val info: MpvNode) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.NEXT_FRAME_INFO
    }

    public data class BlockForTargetTime(val block: Boolean) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.BLOCK_FOR_TARGET_TIME
    }

    public data class SkipRendering(val skip: Boolean) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.SKIP_RENDERING
    }

    public data class DrmDisplay(val display: String) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.DRM_DISPLAY
    }

    public data class DrmDrawSurfaceSize(val size: Int) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.DRM_DRAW_SURFACE_SIZE
    }

    public data class DrmDisplayV2(val display: String) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.DRM_DISPLAY_V2
    }

    public data class SwSize(val size: Int) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.SW_SIZE
    }

    public data class SwFormat(val format: Int) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.SW_FORMAT
    }

    public data class SwStride(val stride: Int) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.SW_STRIDE
    }

    public data class SwPointer(val pointer: Long) : RenderParam {
        override val type: MpvRenderParamType = MpvRenderParamType.SW_POINTER
    }
}