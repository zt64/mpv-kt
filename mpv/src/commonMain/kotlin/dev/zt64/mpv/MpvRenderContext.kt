package dev.zt64.mpv

/**
 * Wrapper for the mpv render context
 *
 */
public expect class MpvRenderContext {
    public fun getInfo(param: MpvRenderParam)

    public fun setParameter(param: MpvRenderParam)

    /**
     * TODO
     *
     * @return A bitset of flags indicating what changed
     *         a bitset of mpv_render_update_flag values (i.e. multiple flags are
     *         combined with bitwise or). Typically, this will tell the API user
     *         what should happen next. E.g. if the [MPV_RENDER_UPDATE_FRAME] flag is
     *         set, [render] should be called. If flags unknown
     *         to the API user are set, or if the return value is 0, nothing needs
     *         to be done.
     */
    public fun update(): ULong

    /**
     * TODO
     *
     * @param params
     */
    public fun render(params: List<MpvRenderParam>)

    /**
     * Set the callback that notifies you when a new video frame is available, or if the video
     * display configuration somehow changed and requires a redraw.
     *
     * @param callback The callback
     */
    public fun setUpdateCallback(callback: () -> Unit)

    /**
     * Close the render context and free all resources
     */
    public fun close()
}