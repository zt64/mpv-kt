package dev.zt64.mpvkt

/**
 * See also: [list-of-input-commands](https://mpv.io/manual/stable/#list-of-input-commands)
 *
 * @param args
 * @return
 */
public expect fun Mpv.command(vararg args: String)

/**
 * TODO: Add documentation
 *
 * @param args
 * @return
 */
public expect fun Mpv.commandNode(vararg args: MpvNode): MpvNode

/**
 * TODO: Add documentation
 *
 * @param args
 * @return
 */
public expect suspend fun Mpv.commandAsync(vararg args: String)

/**
 * TODO: Add documentation
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