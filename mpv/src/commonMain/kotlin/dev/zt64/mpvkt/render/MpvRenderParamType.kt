package dev.zt64.mpvkt.render

public enum class MpvRenderParamType {
    INVALID,
    API_TYPE,
    OPENGL_INIT_PARAMS,
    OPENGL_FBO,
    FLIP_Y,
    DEPTH,
    ICC_PROFILE,
    AMBIENT_LIGHT,
    X11_DISPLAY,
    WL_DISPLAY,
    ADVANCED_CONTROL,
    NEXT_FRAME_INFO,
    BLOCK_FOR_TARGET_TIME,
    SKIP_RENDERING,
    DRM_DISPLAY,
    DRM_DRAW_SURFACE_SIZE,
    DRM_DISPLAY_V2,
    SW_SIZE,
    SW_FORMAT,
    SW_STRIDE,
    SW_POINTER
}

public object MpvRenderApiType {
    public const val MPV_RENDER_API_TYPE_OPENGL: String = "opengl"
    public const val MPV_RENDER_API_TYPE_SW: String = "sw"
}