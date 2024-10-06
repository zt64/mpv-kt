package dev.zt64.mpvkt

public enum class MpvLogLevel(internal val value: Int, private val str: String) {
    NONE(0, "no"),
    FATAL(10, "fatal"),
    ERROR(20, "error"),
    WARN(30, "warn"),
    INFO(40, "info"),
    VERBOSE(50, "v"),
    DEBUG(60, "debug"),
    TRACE(70, "trace");
}