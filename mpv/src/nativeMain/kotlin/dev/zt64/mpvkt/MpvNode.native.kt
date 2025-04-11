package dev.zt64.mpvkt

import kotlinx.cinterop.toKString
import mpv.*

internal fun mpv_node.toKotlin(): MpvNode {
    return when (format) {
        MPV_FORMAT_STRING -> {
            MpvNode.StringNode(u.string!!.toKString())
        }
        MPV_FORMAT_FLAG -> {
            MpvNode.FlagNode(u.flag != 0)
        }
        MPV_FORMAT_INT64 -> {
            MpvNode.LongNode(u.int64)
        }
        MPV_FORMAT_DOUBLE -> {
            MpvNode.DoubleNode(u.double_)
        }
        MPV_FORMAT_NODE_ARRAY -> {
            TODO()
        }
        MPV_FORMAT_NODE_MAP -> {
            TODO()
        }
        else -> {
            throw IllegalArgumentException("Unsupported format: $format")
        }
    }
}