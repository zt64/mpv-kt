#include "jni_utils.h"
#include <mpv/client.h>
#include <clocale>

extern "C" {
#if defined(__ANDROID__)
  #include <libavcodec/jni.h>
#endif
}

extern "C" {
jni_func(jlong, clientApiVersion);

jni_func(jlong, create);
jni_func(int, init, jlong handle);
jni_func(void, destroy, jlong handle);
jni_func(void, wakeup, jlong handle);

jni_func(void, requestLogMessages, jlong handle, jstring level);
jni_func(void, waitAsyncRequests, jlong handle);

jni_func(jobject, requestEvent, jlong handle, jstring event, jint format);
jni_func(jobject, waitEvent, jlong handle, jlong reply);

jni_func(void, command, jlong handle, jobjectArray jarray);
jni_func(void, commandNode, jlong handle, jobjectArray jarray);
jni_func(void, commandRet, jlong handle, jobjectArray jarray);
jni_func(void, commandString, jlong handle, jobjectArray jarray);
jni_func(void, commandAsync, jlong handle, jobjectArray jarray);
jni_func(void, commandNodeAsync, jlong handle, jobjectArray jarray);
jni_func(void, abortAsyncCommand, jlong handle, jlong id);

jni_func(jstring, clientName, jlong handle);
jni_func(jobject, clientId, jlong handle);

jni_func(jlong, getTimeNs, jlong handle);
jni_func(jlong, getTimeUs, jlong handle);

jni_func(void, hookAdd, jlong handle, jlong reply, jstring name_, jint priority);
jni_func(void, hookContinue, jlong handle, jlong id);

jni_func(jstring, getErrorString, jint error);
}

JavaVM *g_vm;

static void prepare_environment(JNIEnv *env) {
    setlocale(LC_NUMERIC, "C");

    if (!env->GetJavaVM(&g_vm) && g_vm) {
        #if defined(__ANDROID__)
        av_jni_set_java_vm(g_vm, nullptr);
        #endif
    }

    init_methods_cache(env);
}

jni_func(jlong, clientApiVersion) {
    return static_cast<jlong>(mpv_client_api_version());
}

jni_func(jlong, create) {
    prepare_environment(env);

    return reinterpret_cast<jlong>(mpv_create());
}

jni_func(int, init, const jlong handle) {
    return mpv_initialize(reinterpret_cast<mpv_handle *>(handle));
}

jni_func(void, destroy, const jlong handle) {
    mpv_terminate_destroy(reinterpret_cast<mpv_handle *>(handle));
}

jni_func(void, wakeup, jlong handle) {
    mpv_wakeup(reinterpret_cast<mpv_handle *>(handle));
}

jni_func(void, requestLogMessages, jlong handle, jstring level) {
    const char *nativeString = env->GetStringUTFChars(level, nullptr);

    mpv_request_log_messages(reinterpret_cast<mpv_handle *>(handle), nativeString);

    env->ReleaseStringUTFChars(level, nativeString);
}

jni_func(void, waitAsyncRequests, jlong handle) {
    mpv_wait_async_requests(reinterpret_cast<mpv_handle *>(handle));
}

// jni_func(jobject, requestEvent, jlong handle, jstring event, jint format) {
//     const char *nativeString = env->GetStringUTFChars(event, nullptr);
//
//     mpv_event *event_ = mpv_request_event(reinterpret_cast<mpv_handle *>(handle), nativeString, format);
//
//     env->ReleaseStringUTFChars(event, nativeString);
//
//     return event_ ? env->NewObject(mpv_MpvEvent, mpv_MpvEvent_init, (jlong) event_) : nullptr;
// }
//
// jni_func(jobject, waitEvent, jlong handle, jlong reply) {
//     mpv_event *event_ = mpv_wait_event(reinterpret_cast<mpv_handle *>(handle), reply);
//
//     return env->NewObject(mpv_MpvEvent, mpv_MpvEvent_init, (jlong) event_);
// }

jni_func(void, command, jlong handle, jobjectArray jarray) {
    const char *arguments[128] = {nullptr};
    const int len = env->GetArrayLength(jarray);

    for (int i = 0; i < len; ++i) {
        arguments[i] = env->GetStringUTFChars((jstring) env->GetObjectArrayElement(jarray, i), nullptr);
    }

    mpv_command(reinterpret_cast<mpv_handle *>(handle), arguments);

    for (int i = 0; i < len; ++i) {
        env->ReleaseStringUTFChars((jstring) env->GetObjectArrayElement(jarray, i), arguments[i]);
    }
}

jni_func(void, commandString, jlong handle, jobjectArray jarray) {
    const char *arguments[128] = {nullptr};
    const int len = env->GetArrayLength(jarray);
}

jni_func(jstring, clientName, jlong handle) {
    return env->NewStringUTF(mpv_client_name(reinterpret_cast<mpv_handle *>(handle)));
}

jni_func(jobject, clientId, jlong handle) {
    const auto id = mpv_client_id(reinterpret_cast<mpv_handle *>(handle));
    return env->NewObject(java_Long, java_Long_init, (jlong) id);
}

jni_func(jlong, getTimeNs, jlong handle) {
    return mpv_get_time_ns(reinterpret_cast<mpv_handle *>(handle));
}

jni_func(jlong, getTimeUs, jlong handle) {
    return mpv_get_time_us(reinterpret_cast<mpv_handle *>(handle));
}

jni_func(void, hookAdd, jlong handle, jlong reply, jstring name_, jint priority) {
    const char *name = env->GetStringUTFChars(name_, nullptr);

    mpv_hook_add(reinterpret_cast<mpv_handle *>(handle), reply, name, priority);

    env->ReleaseStringUTFChars(name_, name);
}

jni_func(void, hookContinue, const jlong handle, const jlong id) {
    mpv_hook_continue(reinterpret_cast<mpv_handle *>(handle), id);
}

jni_func(jstring, getErrorString, jint error) {
    return env->NewStringUTF(mpv_error_string(error));
}