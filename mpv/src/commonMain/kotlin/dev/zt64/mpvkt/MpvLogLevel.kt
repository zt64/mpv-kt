package dev.zt64.mpvkt

public enum class MpvLogLevel(internal val value: Int) {
    NONE(0),
    FATAL(10),
    ERROR(20),
    WARN(30),
    INFO(40),
    VERBOSE(50),
    DEBUG(60),
    TRACE(70)
}