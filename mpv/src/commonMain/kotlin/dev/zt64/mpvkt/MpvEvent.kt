package dev.zt64.mpvkt

public sealed interface MpvEvent {
    public data object Shutdown : MpvEvent

    public data class LogMessage(
        public val prefix: String,
        public val text: String,
        public val level: MpvLogLevel
    ) : MpvEvent

    public data class GetPropertyReply(
        public val code: Int,
        public val name: String,
        public val data: Any?,
        override val replyUserData: Long
    ) : Reply

    public data class SetPropertyReply(public val code: Int, override val replyUserData: Long) : Reply

    public data class CommandReply(
        public val code: Int,
        public val result: MpvNode,
        override val replyUserData: Long
    ) : Reply

    public data class StartFile(public val playlistEntryId: Int) : MpvEvent

    /**
     *
     */
    public data class EndFile(
        public val reason: Reason,
        public val playlistEntryId: Int,
        public val playlistInsertId: Int,
        public val playlistInsertEntries: Int
    ) : MpvEvent {
        /**
         * The reason why the file ended
         */
        public enum class Reason {
            /** End of file reached during normal playback */
            EOF,

            /** Playback was stopped by user or API call */
            STOP,

            /** Application is shutting down */
            QUIT,

            /** An error occurred during playback */
            ERROR,

            /** Playback was redirected to another resource */
            REDIRECT
        }
    }

    public data object FileLoaded

    public data class ClientMessage(public val args: Array<String>) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as ClientMessage

            return args.contentEquals(other.args)
        }

        override fun hashCode(): Int = args.contentHashCode()
    }

    public data object VideoReconfig : MpvEvent

    public data object AudioReconfig : MpvEvent

    public data object Seek : MpvEvent

    public data object PlaybackRestart : MpvEvent

    /**
     * @param name
     */
    public data class PropertyChange(
        public val name: String,
        public val data: Any?,
        override val replyUserData: Long
    ) : Reply

    public data object QueueOverflow : MpvEvent

    /**
     *
     */
    public data class Hook(
        public val name: String,
        public val id: Long,
        public override val replyUserData: Long
    ) : Reply
}

/**
 * A reply event to an asynchronous request
 */
public sealed interface Reply : MpvEvent {
    /**
     * User data for asynchronous replies
     */
    public val replyUserData: Long
}