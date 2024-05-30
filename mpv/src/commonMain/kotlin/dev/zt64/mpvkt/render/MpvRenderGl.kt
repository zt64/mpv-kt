package dev.zt64.mpvkt.render

public expect class MpvOpenGlInitParams

public expect class MpvOpenGlFbo

public expect fun MpvOpenGlFbo(
    fbo: Int,
    w: Int,
    h: Int,
    internalFormat: Int
): MpvOpenGlFbo