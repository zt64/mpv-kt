package dev.zt64.mpvkt

import kotlin.jvm.JvmInline

public expect class MpvNode

public sealed interface MpvNodeValue

@JvmInline
public value class MpvNodeString(public val value: String) : MpvNodeValue

@JvmInline
public value class MpvNodeFlag(public val value: Boolean) : MpvNodeValue