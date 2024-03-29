package dev.zt64.mpv

public actual inline fun Mpv.command(vararg args: String) {
    LibMpv.mpv_command(handle, args).checkError()
}

public actual fun Mpv.commandNode(vararg args: MpvNode): MpvNode {
    TODO()
}

public actual suspend fun Mpv.commandAsync(vararg args: String) {
    LibMpv.mpv_command_async(handle, 0, args).checkError()
    TODO()
}

public actual suspend fun Mpv.commandNodeAsync(vararg args: MpvNode): MpvNode {
    TODO()
}

public actual fun Mpv.abortAsyncCommand(id: ULong) {
    TODO()
}