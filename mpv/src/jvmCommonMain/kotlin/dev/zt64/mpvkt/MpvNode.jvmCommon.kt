package dev.zt64.mpvkt

public actual class MpvNode

// public data object None : MpvNodeValue

public sealed interface MpvNodeValue {
    public val value: Any
}

@JvmInline
public value class MpvNodeString(public override val value: String) : MpvNodeValue

@JvmInline
public value class MpvNodeFlag(public override val value: Boolean) : MpvNodeValue

@JvmInline
public value class MpvNodeLong(public override val value: Long) : MpvNodeValue

@JvmInline
public value class MpvNodeDouble(public override val value: Double) : MpvNodeValue

@JvmInline
public value class MpvNodeArray(public override val value: List<MpvNode>) : MpvNodeValue

@JvmInline
public value class MpvNodeMap(public override val value: Map<String, MpvNodeValue>) : MpvNodeValue

@JvmInline
public value class MpvNodeByte(public override val value: ByteArray) : MpvNodeValue