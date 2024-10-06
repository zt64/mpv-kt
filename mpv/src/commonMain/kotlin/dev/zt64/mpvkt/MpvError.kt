package dev.zt64.mpvkt

public object MpvError {
    public const val SUCCESS: Int = 0
    public const val EVENT_QUEUE_FULL: Int = -1
    public const val NOMEM: Int = -2
    public const val UNINITIALIZED: Int = -3
    public const val INVALID_PARAMETER: Int = -4
    public const val OPTION_NOT_FOUND: Int = -5
    public const val OPTION_FORMAT: Int = -6
    public const val OPTION_ERROR: Int = -7
    public const val PROPERTY_NOT_FOUND: Int = -8
    public const val PROPERTY_FORMAT: Int = -9
    public const val PROPERTY_UNAVAILABLE: Int = -10
    public const val PROPERTY_ERROR: Int = -11
    public const val COMMAND: Int = -12
    public const val LOADING_FAILED: Int = -13
    public const val AO_INIT_FAILED: Int = -14
    public const val VO_INIT_FAILED: Int = -15
    public const val NOTHING_TO_PLAY: Int = -16
    public const val UNKNOWN_FORMAT: Int = -17
    public const val UNSUPPORTED: Int = -18
    public const val NOT_IMPLEMENTED: Int = -19
    public const val GENERIC: Int = -20
}

public class MpvException(message: String) : Exception(message)