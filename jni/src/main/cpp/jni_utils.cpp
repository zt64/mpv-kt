#include "jni_utils.h"

#include <cstdlib>

bool acquire_jni_env(JavaVM *vm, JNIEnv **env) {
    int ret = vm->GetEnv((void **) env, JNI_VERSION_1_6);
    return ret == JNI_EDETACHED ? vm->AttachCurrentThread(reinterpret_cast<void **>(env), nullptr) == 0 : ret == JNI_OK;
}

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
