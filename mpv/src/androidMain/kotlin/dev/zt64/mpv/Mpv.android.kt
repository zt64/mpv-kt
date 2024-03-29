package dev.zt64.mpv

import android.view.Surface

/**
 * Attaches a surface to the mpv instance.
 *
 * @param surface The surface to attach.
 */
public fun Mpv.attachSurface(surface: Surface) {
}

/**
 * Detaches the currently attached surface from the mpv instance.
 */
public fun Mpv.detachSurface() {
    setProperty("wid", 0L)
}