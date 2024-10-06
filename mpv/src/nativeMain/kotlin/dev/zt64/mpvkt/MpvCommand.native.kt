package dev.zt64.mpvkt

import kotlinx.cinterop.*
import mpv.*
import kotlin.random.Random
import kotlin.random.nextULong

public actual suspend fun Mpv.command(vararg args: String) {
    memScoped {
        mpv_command(handle, args.asList().toCStringArray(memScope))
    }.checkError()
}

public actual fun Mpv.commandNode(vararg args: MpvNode): MpvNode = memScoped {
    val result = alloc<mpv_node>()
    mpv_command_node(
        ctx = handle,
        args = allocArrayOfPointersTo(*args).reinterpret(),
        result = result.ptr
    ).checkError()
    return result
}

public actual suspend fun Mpv.commandAsync(vararg args: String) {
    val replyCode = Random.nextULong()
    memScoped {
        mpv_command_async(
            ctx = handle,
            reply_userdata = replyCode,
            args = args.asList().toCStringArray(memScope)
        ).checkError()
        TODO()
    }
}

public actual suspend fun Mpv.commandNodeAsync(vararg args: MpvNode): MpvNode {
    val replyCode = Random.nextULong()
    memScoped {
        mpv_command_node_async(
            ctx = handle,
            reply_userdata = replyCode,
            args = allocArrayOfPointersTo(*args).reinterpret()
        ).checkError()
        TODO()
    }
}

public actual inline fun Mpv.abortAsyncCommand(id: ULong) {
    mpv_abort_async_command(handle, id)
}