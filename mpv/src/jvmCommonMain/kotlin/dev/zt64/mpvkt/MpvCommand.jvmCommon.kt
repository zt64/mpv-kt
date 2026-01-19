package dev.zt64.mpvkt

import kotlinx.coroutines.runBlocking

public actual fun Mpv.command(vararg args: String) {
    LibMpv.command(handle, args)
}

public actual fun <T : MpvNode> Mpv.command(vararg args: MpvNode): T {
    TODO()
}

public actual suspend fun Mpv.commandAsync(vararg args: String) {
    LibMpv.commandAsync(handle, generateReplyId(), args as Array<String>)
}

public actual suspend fun <T : MpvNode> Mpv.commandAsync(vararg args: MpvNode): T {
    TODO()
}

public actual fun Mpv.abortAsyncCommand(id: Long) {
    LibMpv.abortAsyncCommand(handle, id)
}