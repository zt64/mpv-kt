package dev.zt64.mpv

public expect class MpvNode

public sealed interface MpvNodeValue {
    public val format: MpvFormat
}

public class MpvNodeString(public val value: String) : MpvNodeValue {
    override val format: MpvFormat = MpvFormat.STRING
}

public class MpvNodeFlag(public val value: Boolean) : MpvNodeValue {
    override val format: MpvFormat = MpvFormat.FLAG
}