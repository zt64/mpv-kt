#include "jni_utils.h"
#include <mpv/client.h>

//bool acquire_jni_env(JavaVM *vm, JNIEnv **env) {
//    int ret = vm->GetEnv((void **) env, JNI_VERSION_1_6);
//    return ret == JNI_EDETACHED ? vm->AttachCurrentThread(reinterpret_cast<void **>(env), nullptr) == 0 : ret == JNI_OK;
//}

void init_methods_cache(JNIEnv* env) {
    static bool methods_initialized = false;
    if (methods_initialized) return;

#define FIND_CLASS(name) reinterpret_cast<jclass>(env->NewGlobalRef(env->FindClass(name)))

#define FIND_PRIMITIVE(name, value, signature) \
    java_##name = FIND_CLASS("java/lang/" #name); \
    java_##name##_init = env->GetMethodID(java_##name, "<init>", "(" #signature ")V"); \
    java_##name##_##value = env->GetMethodID(java_##name, #value, "()" #signature);

#define FIND_EVENT(event_name, signature) \
    mpv_MpvEvent_##event_name = FIND_CLASS("dev/zt64/mpvkt/MpvEvent$" #event_name); \
    mpv_MpvEvent_##event_name##_init = env->GetMethodID( \
        mpv_MpvEvent_##event_name, \
        "<init>", \
        signature \
    );

#define FIND_NODE(node_name, signature) \
    mpv_MpvNode##node_name = FIND_CLASS("dev/zt64/mpvkt/MpvNode$" #node_name "Node"); \
    mpv_MpvNode##node_name##_init = env->GetMethodID( \
        mpv_MpvNode##node_name, \
        "<init>", \
        signature \
    );

    FIND_PRIMITIVE(Integer, intValue, I)
    FIND_PRIMITIVE(Double, doubleValue, D)
    FIND_PRIMITIVE(Long, longValue, J)
    FIND_PRIMITIVE(Boolean, booleanValue, Z)

    mpv_MPVException = FIND_CLASS("dev/zt64/mpvkt/MpvException");

    mpv_MpvNode = FIND_CLASS("dev/zt64/mpvkt/MpvNode");

    FIND_NODE(String, "(Ljava/lang/String;)V")
    FIND_NODE(Flag, "(Z)V")
    FIND_NODE(Long, "(J)V")
    FIND_NODE(Double, "(D)V")
    FIND_NODE(Array, "(Ljava/util/List;)V")
    FIND_NODE(Map, "(Ljava/util/Map;)V")
    FIND_NODE(Byte, "([B)V")

    mpv_MpvRenderApiType = FIND_CLASS("dev/zt64/mpvkt/render/MpvRenderApiType");
    mpv_MpvRenderApiType_getOrdinal = env->GetMethodID(
        mpv_MpvRenderApiType,
        "ordinal",
        "()I"
    );

    mpv_MpvWakeupCallback = FIND_CLASS("dev/zt64/mpvkt/MpvWakeupCallback");
    mpv_MpvWakeupCallback_invoke = env->GetMethodID(
        mpv_MpvWakeupCallback,
        "invoke",
        "()V"
    );

    mpv_MpvRenderUpdateCallback = FIND_CLASS("dev/zt64/mpvkt/MpvRenderUpdateCallback");
    mpv_MpvRenderUpdateCallback_invoke = env->GetMethodID(
        mpv_MpvRenderUpdateCallback,
        "invoke",
        "()V"
    );

    java_Map = FIND_CLASS("java/util/HashMap");
    java_Map_init = env->GetMethodID(java_Map, "<init>", "()V");
    java_Map_put = env->GetMethodID(java_Map, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    FIND_EVENT(Shutdown, "()V")
    FIND_EVENT(LogMessage, "(Ljava/lang/String;Ljava/lang/String;Ldev/zt64/mpvkt/MpvLogLevel;)V")
    FIND_EVENT(GetPropertyReply, "(ILjava/lang/String;Ljava/lang/Object;J)V")
    FIND_EVENT(SetPropertyReply, "(IJ)V")
    FIND_EVENT(CommandReply, "(ILdev/zt64/mpvkt/MpvNode;J)V")
    FIND_EVENT(StartFile, "(I)V")
    FIND_EVENT(EndFile, "(Ldev/zt64/mpvkt/MpvEvent$EndFile$Reason;III)V")
    FIND_EVENT(FileLoaded, "()V")
    FIND_EVENT(ClientMessage, "([Ljava/lang/String;)V")
    FIND_EVENT(VideoReconfig, "()V")
    FIND_EVENT(AudioReconfig, "()V")
    FIND_EVENT(Seek, "()V")
    FIND_EVENT(PlaybackRestart, "()V")
    FIND_EVENT(PropertyChange, "(Ljava/lang/String;Ljava/lang/Object;J)V")
    FIND_EVENT(QueueOverflow, "()V")
    FIND_EVENT(Hook, "(Ljava/lang/String;JJ)V")

#undef FIND_PRIMITIVE
#undef FIND_EVENT
#undef FIND_NODE
#undef FIND_CLASS

    methods_initialized = true;
}

void handleMpvError(JNIEnv* env, const int error) {
    if (error < 0) env->ThrowNew(mpv_MPVException, mpv_error_string(error));
}