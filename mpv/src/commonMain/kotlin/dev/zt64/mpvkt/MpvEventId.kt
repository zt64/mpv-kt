package dev.zt64.mpvkt

public enum class MpvEventId(internal val id: Int) {
    NONE(0),
    SHUTDOWN(1),
    LOG_MESSAGE(2),
    GET_PROPERTY_REPLY(3),
    SET_PROPERTY_REPLY(4),
    COMMAND_REPLY(5),
    START_FILE(6),
    END_FILE(7),
    FILE_LOADED(8),

    @Deprecated("")
    IDLE(11),

    @Deprecated("")
    TICK(14),
    CLIENT_MESSAGE(16),
    VIDEO_RECONFIG(17),
    AUDIO_RECONFIG(18),
    SEEK(20),
    PLAYBACK_RESTART(21),
    PROPERTY_CHANGE(22),
    QUEUE_OVERFLOW(24),
    HOOK(25)
}