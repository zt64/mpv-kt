package dev.zt64.mpv

import com.sun.jna.Pointer
import com.sun.jna.Structure

@Structure.FieldOrder("event_id", "error", "reply_userdata", "data")
public actual class MpvEvent : Structure() {
    @JvmField
    public var event_id: Int = 0

    @JvmField
    public var error: Int = 0

    @JvmField
    public var reply_userdata: Long = 0

    @JvmField
    public var data: Pointer? = null
}