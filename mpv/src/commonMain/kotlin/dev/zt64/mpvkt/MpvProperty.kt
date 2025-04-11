package dev.zt64.mpvkt

/**
 * See also: [property-list](https://mpv.io/manual/stable/#property-list)
 *           [options](https://mpv.io/manual/master/#options)
 *
 * @param T The type of the property, must be one of [MpvNode]
 * @param name The name of the property
 * @return The value of the property
 */
public inline fun <reified T : Any> Mpv.getProperty(name: String): T? = when (T::class) {
    String::class -> getPropertyString(name)
    Boolean::class -> getPropertyFlag(name)
    Long::class -> getPropertyLong(name)
    Double::class -> getPropertyDouble(name)
    List::class -> getPropertyArray(name)
    Map::class -> getPropertyMap(name)
    ByteArray::class -> getPropertyByteArray(name)
    else -> throw IllegalArgumentException("Unsupported property type: ${T::class}")
} as T?

public expect fun Mpv.getPropertyString(name: String): String?

public expect fun Mpv.getPropertyFlag(name: String): Boolean?

public expect fun Mpv.getPropertyLong(name: String): Long?

public expect fun Mpv.getPropertyDouble(name: String): Double?

public expect fun Mpv.getPropertyArray(name: String): List<MpvNode>?

public expect fun Mpv.getPropertyMap(name: String): Map<String, MpvNode>?

public expect fun Mpv.getPropertyByteArray(name: String): ByteArray?

public expect fun Mpv.setOption(name: String, value: String)

public expect fun Mpv.setProperty(name: String, value: String)

public expect fun Mpv.setProperty(name: String, value: Boolean)

public expect fun Mpv.setProperty(name: String, value: Long)

public expect fun Mpv.setProperty(name: String, value: Double)

public expect fun Mpv.setProperty(name: String, value: List<MpvNode>)

public expect fun Mpv.setProperty(name: String, value: Map<String, MpvNode>)

public expect fun Mpv.setProperty(name: String, value: ByteArray)

public expect fun <T> Mpv.observeProperty(name: String, callback: (T) -> Unit)

public expect fun Mpv.unobserveProperty(name: String)

public expect fun Mpv.delProperty(name: String)