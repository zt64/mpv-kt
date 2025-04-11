package dev.zt64.mpvkt

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.test.Test

class CbTest {
    @Test
    fun testCb() = runMpvTest { mpv ->
        mpv.streamCbAddRo("myprotocol") { uri ->
            val path = Path(uri)
            MpvStreamCbInfo(
                cookie = SystemFileSystem.source(path).buffered(),
                read = { cookie, buffer, nBytes ->
                    TODO()
                },
                seek = { cookie, offset ->
                    TODO()
                },
                size = {
                    TODO()
                },
                close = { cookie -> cookie.close() }
            )
        }

        mpv.command("loadfile", "myprotocol://fake")

        mpv.close()
    }
}