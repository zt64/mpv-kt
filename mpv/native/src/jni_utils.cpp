#include "jni_utils.h"

#include <cstdlib>
#include <mpv/client.h>

//bool acquire_jni_env(JavaVM *vm, JNIEnv **env) {
//    int ret = vm->GetEnv((void **) env, JNI_VERSION_1_6);
//    return ret == JNI_EDETACHED ? vm->AttachCurrentThread(reinterpret_cast<void **>(env), nullptr) == 0 : ret == JNI_OK;
//}

#define PRIMITIVE(name, value) \
jclass java_##name;     \
jmethodID java_##name##_init, java_##name##_##value;

PRIMITIVE(Integer, intValue)
PRIMITIVE(Double, doubleValue)
PRIMITIVE(Long, longValue)
PRIMITIVE(Boolean, booleanValue)

#undef PRIMITIVE

jclass mpv_MPVLib;
jmethodID mpv_MPVLib_eventProperty_S, mpv_MPVLib_eventProperty_Sb, mpv_MPVLib_eventProperty_Sl, mpv_MPVLib_eventProperty_SS, mpv_MPVLib_event, mpv_MPVLib_logMessage_SiS;

void init_methods_cache(JNIEnv *env) {
    static bool methods_initialized = false;
    if (methods_initialized) return;

#define FIND_CLASS(name) reinterpret_cast<jclass>(env->NewGlobalRef(env->FindClass(name)))
#define FIND_PRIMITIVE(name, value, signature) \
    java_##name = FIND_CLASS("java/lang/" #name); \
    java_##name##_init = env->GetMethodID(java_##name, "<init>", "(" #signature ")V"); \
    java_##name##_##value = env->GetMethodID(java_##name, #value, "()" #signature);

    FIND_PRIMITIVE(Integer, intValue, I)
    FIND_PRIMITIVE(Double, doubleValue, D)
    FIND_PRIMITIVE(Long, longValue, J)
    FIND_PRIMITIVE(Boolean, booleanValue, Z)
#undef FIND_PRIMITIVE

    mpv_MPVLib = FIND_CLASS("dev/zt64/mpvkt/LibMpv");

    mpv_MPVException = FIND_CLASS("dev/zt64/mpvkt/MpvException");

    mpv_MpvNode = FIND_CLASS("dev/zt64/mpvkt/MpvNodeValue");
    mpv_MpvNodeString = FIND_CLASS("dev/zt64/mpvkt/MpvNodeString");
    mpv_MpvNodeString_init = env->GetMethodID(
            mpv_MpvNodeString,
            "<init>",
            "(Ljava/lang/String;)V"
    );

    mpv_MpvNodeFlag = FIND_CLASS("dev/zt64/mpvkt/MpvNodeFlag");
    mpv_MpvNodeFlag_init = env->GetMethodID(
            mpv_MpvNodeFlag,
            "<init>",
            "(Z)V"
    );

    mpv_MpvNodeLong = FIND_CLASS("dev/zt64/mpvkt/MpvNodeLong");
    mpv_MpvNodeLong_init = env->GetMethodID(
            mpv_MpvNodeLong,
            "<init>",
            "(J)V"
    );

    mpv_MpvNodeDouble = FIND_CLASS("dev/zt64/mpvkt/MpvNodeDouble");
    mpv_MpvNodeDouble_init = env->GetMethodID(
            mpv_MpvNodeDouble,
            "<init>",
            "(D)V"
    );

    mpv_MpvNodeArray = FIND_CLASS("dev/zt64/mpvkt/MpvNodeArray");
    mpv_MpvNodeArray_init = env->GetMethodID(
            mpv_MpvNodeArray,
            "<init>",
            "(Ljava/util/List;)V"
    );

    mpv_MpvNodeMap = FIND_CLASS("dev/zt64/mpvkt/MpvNodeMap");
    mpv_MpvNodeMap_init = env->GetMethodID(
            mpv_MpvNodeMap,
            "<init>",
            "(Ljava/util/Map;)V"
    );

    mpv_MpvNodeByte = FIND_CLASS("dev/zt64/mpvkt/MpvNodeByte");
    mpv_MpvNodeByte_init = env->GetMethodID(
            mpv_MpvNodeByte,
            "<init>",
            "([B)V"
    );

    java_Map = FIND_CLASS("java/util/HashMap");
    java_Map_init = env->GetMethodID(
            java_Map,
            "<init>",
            "()V"
    );
    java_Map_put = env->GetMethodID(
            java_Map,
            "put",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
    );
//    mpv_MPVLib_eventProperty_S = env->GetStaticMethodID(
//            mpv_MPVLib, "eventProperty",
//            "(Ljava/lang/String;)V"
//    ); // eventProperty(String)
//    mpv_MPVLib_eventProperty_Sb = env->GetStaticMethodID(
//            mpv_MPVLib, "eventProperty",
//            "(Ljava/lang/String;Z)V"
//    ); // eventProperty(String, boolean)
//    mpv_MPVLib_eventProperty_Sl = env->GetStaticMethodID(
//            mpv_MPVLib, "eventProperty",
//            "(Ljava/lang/String;J)V"
//    ); // eventProperty(String, long)
//    mpv_MPVLib_eventProperty_SS = env->GetStaticMethodID(
//            mpv_MPVLib, "eventProperty",
//            "(Ljava/lang/String;Ljava/lang/String;)V"
//    ); // eventProperty(String, String)
//    mpv_MPVLib_event = env->GetStaticMethodID(mpv_MPVLib, "event", "(I)V"); // event(int)
#undef FIND_CLASS

    methods_initialized = true;
}

void handleMpvError(JNIEnv *env, int error) {
    if (error < 0) {
        env->ThrowNew(mpv_MPVException, mpv_error_string(error));
    }
}
