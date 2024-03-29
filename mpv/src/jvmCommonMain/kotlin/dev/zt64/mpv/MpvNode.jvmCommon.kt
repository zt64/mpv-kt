package dev.zt64.mpv

import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.Structure.FieldOrder
import com.sun.jna.Union

@FieldOrder("format", "u")
public actual class MpvNode : Structure() {
    @JvmField
    public var u: MpvNodeUnion? = null

    @JvmField
    public var format: Int = 0

    override fun read() {
        super.read()
        when (MpvFormat.entries[format]) {
            MpvFormat.NONE -> u = null
            MpvFormat.STRING -> u!!.setType(MpvNodeUnion::string::class.java)
            MpvFormat.OSD_STRING -> u!!.setType(MpvNodeUnion::string::class.java)
            MpvFormat.FLAG -> u!!.setType(MpvNodeUnion::flag::class.java)
            MpvFormat.INT64 -> u!!.setType(MpvNodeUnion::int64::class.java)
            MpvFormat.DOUBLE -> u!!.setType(MpvNodeUnion::double::class.java)
            MpvFormat.NODE -> u!!.setType(MpvNodeUnion::list::class.java)
            MpvFormat.NODE_ARRAY -> u!!.setType(MpvNodeUnion::list::class.java)
            MpvFormat.NODE_MAP -> u!!.setType(MpvNodeUnion::list::class.java)
            MpvFormat.BYTE_ARRAY -> u!!.setType(MpvNodeUnion::ba::class.java)
        }
        u!!.read()
    }
}

public class MpvNodeUnion : Union() {
    @JvmField
    public var string: String? = null

    @JvmField
    public var flag: Int? = null

    @JvmField
    public var int64: Long? = null

    @JvmField
    public var double: Double? = null

    @JvmField
    public var list: MpvNodeList? = null

    @JvmField
    public var ba: MpvByteArray? = null
}

@FieldOrder("num", "values")
public class MpvNodeList : Structure() {
    @JvmField
    public var num: Int = 0

    @JvmField
    public var values: Array<MpvNode> = emptyArray()
}

@FieldOrder("num", "keys", "values")
public class MpvNodeMap : Structure() {
    @JvmField
    public var num: Int = 0

    @JvmField
    public var keys: Array<String> = emptyArray()

    @JvmField
    public var values: Array<MpvNode> = emptyArray()
}

@FieldOrder("data", "size")
public class MpvByteArray : Structure() {
    @JvmField
    public var data: Pointer? = null

    @JvmField
    public var size: Int = 0
}