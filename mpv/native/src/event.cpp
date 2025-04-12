#include "jni_utils.h"
#include <mpv/client.h>

jni_func(jobject, waitEvent, const jlong handle, const jdouble timeout) {
    const mpv_event* event_ = mpv_wait_event(reinterpret_cast<mpv_handle *>(handle), timeout);

    jobject mpvEvent = nullptr;

    switch (event_->event_id) {
        case MPV_EVENT_NONE:
            break;
        case MPV_EVENT_SHUTDOWN:
            mpvEvent = env->NewObject(mpv_MpvEvent_Shutdown, mpv_MpvEvent_Shutdown_init);
            break;
        case MPV_EVENT_LOG_MESSAGE: {
            const auto data = static_cast<mpv_event_log_message *>(event_->data);
            const auto logLevel = env->NewObject(mpv_MpvLogLevel, mpv_MpvLogLevel_init, data->level);
            mpvEvent = env->NewObject(
                mpv_MpvEvent_LogMessage,
                mpv_MpvEvent_LogMessage_init,
                env->NewStringUTF(data->prefix),
                env->NewStringUTF(data->text),
                logLevel
            );
            break;
        }
        case MPV_EVENT_GET_PROPERTY_REPLY: {
            const auto data = static_cast<mpv_event_property *>(event_->data);
            mpvEvent = env->NewObject(
                mpv_MpvEvent_GetPropertyReply,
                mpv_MpvEvent_GetPropertyReply_init,
                event_->error,
                env->NewStringUTF(data->name),
                nullptr,
                event_->reply_userdata
            );
            break;
        }
        case MPV_EVENT_SET_PROPERTY_REPLY: {
            mpvEvent = env->NewObject(
                mpv_MpvEvent_SetPropertyReply,
                mpv_MpvEvent_SetPropertyReply_init,
                event_->error,
                event_->reply_userdata
            );
            break;
        }
        case MPV_EVENT_COMMAND_REPLY: {
            const auto data = static_cast<mpv_event_command *>(event_->data);
            mpvEvent = env->NewObject(
                mpv_MpvEvent_CommandReply,
                mpv_MpvEvent_CommandReply_init,
                event_->error,
                nodeToJobject(env, data->result),
                event_->reply_userdata
            );
            break;
        }
        case MPV_EVENT_START_FILE: {
            const auto data = static_cast<mpv_event_start_file *>(event_->data);
            mpvEvent = env->NewObject(
                mpv_MpvEvent_StartFile,
                mpv_MpvEvent_StartFile_init,
                data->playlist_entry_id
            );
            break;
        }
        case MPV_EVENT_END_FILE: {
            const auto endFile = static_cast<mpv_event_end_file *>(event_->data);
            const char* name = nullptr;

            switch (endFile->reason) {
                case MPV_END_FILE_REASON_EOF:
                    name = "EOF";
                    break;
                case MPV_END_FILE_REASON_STOP:
                    name = "STOP";
                    break;
                case MPV_END_FILE_REASON_QUIT:
                    name = "QUIT";
                    break;
                case MPV_END_FILE_REASON_ERROR:
                    name = "ERROR";
                    break;
                case MPV_END_FILE_REASON_REDIRECT:
                    name = "REDIRECT";
                    break;
            }

            jfieldID reasonId = env->GetStaticFieldID(
                mpv_MpvEvent_EndFile_Reason,
                name,
                "Ldev/zt64/mpvkt/MpvEvent$EndFile$Reason;"
            );

            mpvEvent = env->NewObject(
                mpv_MpvEvent_EndFile,
                mpv_MpvEvent_EndFile_init,
                env->GetStaticObjectField(mpv_MpvEvent_EndFile_Reason, reasonId),
                endFile->playlist_entry_id,
                endFile->playlist_insert_id,
                endFile->playlist_insert_num_entries
            );
            break;
        }
        case MPV_EVENT_FILE_LOADED:
            mpvEvent = env->NewObject(mpv_MpvEvent_FileLoaded, mpv_MpvEvent_FileLoaded_init);
            break;
        case MPV_EVENT_IDLE:
        case MPV_EVENT_TICK:
            break; // Deprecated
        case MPV_EVENT_CLIENT_MESSAGE: {
            const auto data = static_cast<mpv_event_client_message *>(event_->data);

            jobjectArray args = env->NewObjectArray(
                data->num_args,
                env->FindClass("java/lang/String"),
                nullptr
            );

            for (int i = 0; i < data->num_args; i++) {
                env->SetObjectArrayElement(
                    args,
                    i,
                    env->NewStringUTF(data->args[i])
                );
            }

            mpvEvent = env->NewObject(mpv_MpvEvent_ClientMessage, mpv_MpvEvent_ClientMessage_init, args);
            break;
        }
        case MPV_EVENT_VIDEO_RECONFIG:
            mpvEvent = env->NewObject(mpv_MpvEvent_VideoReconfig, mpv_MpvEvent_VideoReconfig_init);
            break;
        case MPV_EVENT_AUDIO_RECONFIG:
            mpvEvent = env->NewObject(mpv_MpvEvent_AudioReconfig, mpv_MpvEvent_AudioReconfig_init);
            break;
        case MPV_EVENT_SEEK:
            mpvEvent = env->NewObject(mpv_MpvEvent_Seek, mpv_MpvEvent_Seek_init);
            break;
        case MPV_EVENT_PLAYBACK_RESTART:
            mpvEvent = env->NewObject(mpv_MpvEvent_PlaybackRestart, mpv_MpvEvent_PlaybackRestart_init);
            break;
        case MPV_EVENT_PROPERTY_CHANGE: {
            const auto propertyChange = static_cast<mpv_event_property *>(event_->data);
            const jobject data = nullptr;

            mpvEvent = env->NewObject(
                mpv_MpvEvent_PropertyChange,
                mpv_MpvEvent_PropertyChange_init,
                env->NewStringUTF(propertyChange->name),
                data,
                event_->reply_userdata
            );
            break;
        }
        case MPV_EVENT_QUEUE_OVERFLOW:
            mpvEvent = env->NewObject(mpv_MpvEvent_QueueOverflow, mpv_MpvEvent_QueueOverflow_init);
            break;
        case MPV_EVENT_HOOK: {
            const auto data = static_cast<mpv_event_hook *>(event_->data);
            mpvEvent = env->NewObject(
                mpv_MpvEvent_Hook,
                mpv_MpvEvent_Hook_init,
                env->NewStringUTF(data->name),
                data->id,
                event_->reply_userdata
            );
            break;
        }
    }

    return mpvEvent;
}