package dev.zt64.mpv

/**
 * See also: [list-of-input-commands](https://mpv.io/manual/stable/#list-of-input-commands)
 *
 * @param args
 * @return
 */
public expect inline fun Mpv.command(vararg args: String)

/**
 * TODO
 *
 * @param args
 * @return
 */
public expect fun Mpv.commandNode(vararg args: MpvNode): MpvNode

/**
 * TODO
 *
 * @param args
 * @return
 */
public expect suspend fun Mpv.commandAsync(vararg args: String)

/**
 * TODO
 *
 * @param args
 * @return
 */
public expect suspend fun Mpv.commandNodeAsync(vararg args: MpvNode): MpvNode

/**
 * Aborts a queued asynchronous command.
 *
 * @param id
 */
public expect fun Mpv.abortAsyncCommand(id: ULong)