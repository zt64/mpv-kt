package dev.zt64.mpv

public object MpvLogLevel {
    public const val MPV_LOG_LEVEL_NONE: Int = 0
    public const val MPV_LOG_LEVEL_FATAL: Int = 10
    public const val MPV_LOG_LEVEL_ERROR: Int = 20
    public const val MPV_LOG_LEVEL_WARN: Int = 30
    public const val MPV_LOG_LEVEL_INFO: Int = 40
    public const val MPV_LOG_LEVEL_V: Int = 50
    public const val MPV_LOG_LEVEL_DEBUG: Int = 60
    public const val MPV_LOG_LEVEL_TRACE: Int = 70
}

public enum class LogLevel(internal val value: Int) {
    NONE(0),
    FATAL(10),
    ERROR(20),
    WARN(30),
    INFO(40),
    VERBOSE(50),
    DEBUG(60),
    TRACE(70)
}