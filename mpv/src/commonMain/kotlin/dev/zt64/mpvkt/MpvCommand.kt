package dev.zt64.mpvkt

import kotlin.random.Random

internal fun generateReplyId() = Random.nextLong()

/**
 * See also: [list-of-input-commands](https://mpv.io/manual/stable/#list-of-input-commands)
 *
 * @param args
 * @return
 */
public expect suspend fun Mpv.command(vararg args: String)

/**
 * TODO: Add documentation
 *
 * @param args
 * @return
 */
public expect fun <T : MpvNode> Mpv.command(vararg args: MpvNode): T

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
public expect suspend fun <T : MpvNode> Mpv.commandAsync(vararg args: MpvNode): T

/**
 * Aborts a queued asynchronous command.
 *
 * @param id
 */
public expect fun Mpv.abortAsyncCommand(id: Long)