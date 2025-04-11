#include "jni_utils.h"
#include <mpv/client.h>

jni_func(jobject, waitEvent, const jlong handle, const jdouble timeout) {
    const mpv_event* event_ = mpv_wait_event(reinterpret_cast<mpv_handle *>(handle), timeout);

    jobject mpvEvent = nullptr;

    switch (event_->event_id) {
        case MPV_EVENT_NONE:
            return nullptr;
        case MPV_EVENT_SHUTDOWN:
            mpvEvent = env->NewObject(mpv_MpvEvent_Shutdown, mpv_MpvEvent_Shutdown_init);
            break;
        case MPV_EVENT_LOG_MESSAGE: {
            const auto logMessage = static_cast<mpv_event_log_message *>(event_->data);
            const auto logLevel = env->NewObject(
                mpv_MpvLogLevel,
                mpv_MpvLogLevel_init,
                logMessage->level
            );
            mpvEvent = env->NewObject(
                mpv_MpvEvent_LogMessage,
                mpv_MpvEvent_LogMessage_init,
                env->NewStringUTF(logMessage->prefix),
                env->NewStringUTF(logMessage->text),
                logLevel
            );
            break;
        }
        case MPV_EVENT_GET_PROPERTY_REPLY:
            break;
        case MPV_EVENT_SET_PROPERTY_REPLY:
            break;
        case MPV_EVENT_COMMAND_REPLY:
            break;
        case MPV_EVENT_START_FILE: {
            const auto startFile = static_cast<mpv_event_start_file *>(event_->data);
            mpvEvent = env->NewObject(
                mpv_MpvEvent_StartFile,
                mpv_MpvEvent_StartFile_init,
                startFile->playlist_entry_id
            );
            break;
        }
        case MPV_EVENT_END_FILE:
            break;
        case MPV_EVENT_FILE_LOADED:
            mpvEvent = env->NewObject(mpv_MpvEvent_FileLoaded, mpv_MpvEvent_FileLoaded_init);
            break;
        case MPV_EVENT_IDLE:
            break;
        case MPV_EVENT_TICK:
            break;
        case MPV_EVENT_CLIENT_MESSAGE: {
            const auto clientMessage = static_cast<mpv_event_client_message *>(event_->data);

            jobjectArray args = env->NewObjectArray(
                clientMessage->num_args,
                env->FindClass("java/lang/String"),
                nullptr
            );

            for (int i = 0; i < clientMessage->num_args; i++) {
                env->SetObjectArrayElement(
                    args,
                    i,
                    env->NewStringUTF(clientMessage->args[i])
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
        case MPV_EVENT_PROPERTY_CHANGE:
            break;
        case MPV_EVENT_QUEUE_OVERFLOW:
            mpvEvent = env->NewObject(mpv_MpvEvent_QueueOverflow, mpv_MpvEvent_QueueOverflow_init);
            break;
        case MPV_EVENT_HOOK: {
            const auto hook = static_cast<mpv_event_hook *>(event_->data);
            mpvEvent = env->NewObject(
                mpv_MpvEvent_Hook,
                mpv_MpvEvent_Hook_init,
                env->NewStringUTF(hook->name),
                hook->id,
                event_->reply_userdata
            );
            break;
        }
    }

    return mpvEvent;
}