package dev.zt64.mpvkt

/**
 * Callback interface for the [Mpv.setWakeupCallback] function.
 */
public fun interface MpvWakeupCallback {
    public operator fun invoke()
}