package dev.zt64.mpvkt

public sealed interface MpvEvent {
    /**
     * A reply event to an asynchronous request
     *
     */
    public interface Reply {
        /**
         * User data for asynchronous replies
         */
        public val replyUserData: Int
    }

    public data class Error(val code: Int) : MpvEvent
}

public data class StartFile(val playlistEntryId: Int) : MpvEvent

public open class EndFile(
    public val reason: Reason,
    public val playlistEntryId: Int,
    public val playlistInsertId: Int,
    public val playlistInsertEntries: Int
) : MpvEvent {
    public enum class Reason(internal val value: Int) {
        EOF(0),
        STOP(2),
        QUIT(3),
        ERROR(4),
        REDIRECT(5)
    }
}