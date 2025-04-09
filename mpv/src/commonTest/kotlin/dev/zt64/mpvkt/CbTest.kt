package dev.zt64.mpvkt

import kotlin.test.Test

class CbTest {
    @Test
    fun testCb() = runMpvTest { mpv ->
        // mpv.streamCbAddRo("myprotocol") { uri ->
        //     val path = Path(uri)
        //     MpvStreamCbInfo(
        //         cookie = SystemFileSystem.source(path).buffered(),
        //         read = { cookie, buffer, nBytes ->
        //             cookie.readTo(buffer, 0, nBytes.toInt())
        //             0
        //         },
        //         seek = { cookie, offset ->
        //             cookie.skip(offset)
        //             0
        //         },
        //         size = {
        //             SystemFileSystem.metadataOrNull(path)?.size ?: 0
        //         },
        //         close = { cookie -> cookie.close() }
        //     )
        // }
        //
        // mpv.command("loadfile", "myprotocol://fake")

        mpv.close()
    }
}