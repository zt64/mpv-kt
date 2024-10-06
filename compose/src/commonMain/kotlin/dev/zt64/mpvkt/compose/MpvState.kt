package dev.zt64.mpvkt.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import dev.zt64.mpvkt.Mpv
import dev.zt64.mpvkt.MpvHandle

@Composable
public fun rememberMpvState(url: String): MpvState {
    return rememberSaveable(saver = MpvState.Saver) { MpvState() }
}

@Composable
public fun rememberMpvState(): MpvState {
    return rememberSaveable(saver = MpvState.Saver) { MpvState() }
}

/**
 * TODO: Add documentation
 *
 */
@Stable
public class MpvState(private val handle: MpvHandle? = null) {
    private val mpv = if (handle != null) Mpv(handle) else Mpv()

    public var position: Float by mutableStateOf(0f)
        private set

    public var duration: Float by mutableStateOf(0f)
        private set

    public var volume: Float by mutableStateOf(1f)
        private set

    public var paused: Boolean by mutableStateOf(false)

    public companion object {
        public val Saver: Saver<MpvState, *> = Saver(
            save = { it.handle },
            restore = { MpvState(it) }
        )
    }
}