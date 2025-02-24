package dev.zt64.mpvkt

import kotlin.jvm.JvmInline

public sealed interface MpvNode {
    public val value: Any

    @JvmInline
    public value class StringNode(public override val value: String) : MpvNode {
        override fun toString(): String = value
    }

    @JvmInline
    public value class FlagNode(public override val value: Boolean) : MpvNode {
        override fun toString(): String = value.toString()
    }

    @JvmInline
    public value class LongNode(public override val value: Long) : MpvNode {
        override fun toString(): String = value.toString()
    }

    @JvmInline
    public value class DoubleNode(public override val value: Double) : MpvNode {
        override fun toString(): String = value.toString()
    }

    @JvmInline
    public value class ArrayNode(public override val value: List<MpvNode>) : MpvNode {
        override fun toString(): String = value.toString()
    }

    @JvmInline
    public value class MapNode(public override val value: Map<String, MpvNode>) : MpvNode {
        override fun toString(): String = value.toString()
    }

    @JvmInline
    public value class ByteNode(public override val value: ByteArray) : MpvNode {
        override fun toString(): String = value.toString()
    }
}