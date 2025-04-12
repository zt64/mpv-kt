package dev.zt64.mpvkt

import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCStringArray
import mpv.mpv_abort_async_command
import mpv.mpv_command
import mpv.mpv_command_async

public actual suspend fun Mpv.command(vararg args: String) {
    memScoped {
        mpv_command(handle, args.asList().toCStringArray(memScope))
    }.checkError()
}

public actual fun <T : MpvNode> Mpv.command(vararg args: MpvNode): T = memScoped {
    // val result = alloc<mpv_node>()
    // mpv_command_node(
    //     ctx = handle,
    //     args = allocArrayOfPointersTo(*args).reinterpret(),
    //     result = result.ptr
    // ).checkError()
    // return result
    TODO()
}

public actual suspend fun Mpv.commandAsync(vararg args: String) {
    val replyCode = generateReplyId().toULong()
    memScoped {
        mpv_command_async(
            ctx = handle,
            reply_userdata = replyCode,
            args = args.asList().toCStringArray(memScope)
        ).checkError()
        TODO()
    }
}

public actual suspend fun <T : MpvNode> Mpv.commandAsync(vararg args: MpvNode): T {
    // val replyCode = generateReplyId().toULong()
    // memScoped {
    //     mpv_command_node_async(
    //         ctx = handle,
    //         reply_userdata = replyCode,
    //         args = allocArrayOfPointersTo(*args).reinterpret()
    //     ).checkError()
    // }
    TODO()
}

public actual fun Mpv.abortAsyncCommand(id: Long) {
    mpv_abort_async_command(handle, id.toULong())
}