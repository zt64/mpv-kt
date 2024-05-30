package dev.zt64.mpvkt.render

import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import mpv.mpv_opengl_fbo

public actual class MpvOpenGlInitParams
public actual typealias MpvOpenGlFbo = mpv_opengl_fbo

public actual fun MpvOpenGlFbo(
    fbo: Int,
    w: Int,
    h: Int,
    internalFormat: Int
): MpvOpenGlFbo {
    return nativeHeap.alloc<mpv_opengl_fbo> {
        this.fbo = fbo
        this.w = w
        this.h = h
    }
}