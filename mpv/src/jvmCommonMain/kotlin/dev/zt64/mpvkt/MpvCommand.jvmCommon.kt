package dev.zt64.mpvkt

import kotlinx.coroutines.runBlocking

public actual suspend fun Mpv.command(vararg args: String) {
    runBlocking {
        LibMpv.command(handle, args as Array<String>)
    }
}

public actual fun Mpv.commandNode(vararg args: MpvNode): MpvNode {
    TODO()
}

public actual suspend fun Mpv.commandAsync(vararg args: String) {
    LibMpv.commandAsync(handle, 0, args as Array<String>)
    TODO()
}

public actual suspend fun Mpv.commandNodeAsync(vararg args: MpvNode): MpvNode {
    TODO()
}

public actual fun Mpv.abortAsyncCommand(id: ULong) {
    LibMpv.abortAsyncCommand(handle, id.toLong())
}