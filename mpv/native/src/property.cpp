#include <mpv/client.h>
#include "jni_utils.h"

jni_func(jint, setOption, const jlong handle, jstring option, jstring value) {
    const char* opt = env->GetStringUTFChars(option, nullptr);
    const char* val = env->GetStringUTFChars(value, nullptr);

    const int ret = mpv_set_option_string(reinterpret_cast<mpv_handle *>(handle), opt, val);

    env->ReleaseStringUTFChars(option, opt);
    env->ReleaseStringUTFChars(value, val);

    return ret;
}

static void common_get_property(
    JNIEnv* env,
    const jlong handle,
    jstring property,
    const mpv_format format,
    void* output
) {
    const char* prop = env->GetStringUTFChars(property, nullptr);
    const int err = mpv_get_property(reinterpret_cast<mpv_handle *>(handle), prop, format, output);
    env->ReleaseStringUTFChars(property, prop);

    handleMpvError(env, err);
}

static int common_set_property(
    JNIEnv* env,
    const jlong handle,
    jstring property,
    const mpv_format format,
    void* value
) {
    const char* prop = env->GetStringUTFChars(property, nullptr);
    const int result = mpv_set_property(reinterpret_cast<mpv_handle *>(handle), prop, format, value);
    env->ReleaseStringUTFChars(property, prop);

    return result;
}

jni_func(jobject, getPropertyLong, const jlong handle, jstring property) {
    int64_t value = 0;
    common_get_property(env, handle, property, MPV_FORMAT_INT64, &value);
    return env->NewObject(java_Long, java_Long_init, value);
}

jni_func(void, setPropertyLong, const jlong handle, jstring property, jlong value) {
    common_set_property(env, handle, property, MPV_FORMAT_INT64, &value);
}

jni_func(jobject, getPropertyDouble, const jlong handle, const jstring property) {
    double value = 0;
    common_get_property(env, handle, property, MPV_FORMAT_DOUBLE, &value);
    return env->NewObject(java_Double, java_Double_init, value);
}

jni_func(void, setPropertyDouble, const jlong handle, const jstring property, jdouble value) {
    common_set_property(env, handle, property, MPV_FORMAT_DOUBLE, &value);
}

jni_func(jobject, getPropertyFlag, const jlong handle, jstring property) {
    int value = 0;
    common_get_property(env, handle, property, MPV_FORMAT_FLAG, &value);
    return env->NewObject(java_Boolean, java_Boolean_init, static_cast<jboolean>(value));
}

jni_func(void, setPropertyFlag, const jlong handle, jstring property, jboolean value) {
    common_set_property(env, handle, property, MPV_FORMAT_FLAG, &value);
}

jni_func(jstring, getPropertyString, const jlong handle, jstring property) {
    char* value = nullptr;
    common_get_property(env, handle, property, MPV_FORMAT_STRING, &value);
    const jstring&ret = env->NewStringUTF(value);
    mpv_free(value);
    return ret;
}

jni_func(void, setPropertyString, const jlong handle, jstring property, jstring jvalue) {
    const char* value = env->GetStringUTFChars(jvalue, nullptr);
    common_set_property(env, handle, property, MPV_FORMAT_STRING, &value);
    env->ReleaseStringUTFChars(jvalue, value);
}

jni_func(jobjectArray, getPropertyArray, const jlong handle, const jstring property) {
    mpv_node value;
    common_get_property(env, handle, property, MPV_FORMAT_NODE, &value);
    return arrayToJvm(env, value);
}

jni_func(jobject, getPropertyMap, const jlong handle, const jstring property) {
    mpv_node value;
    common_get_property(env, handle, property, MPV_FORMAT_NODE, &value);
    return mapToJvm(env, value);
}

jni_func(void, observeProperty, const jlong handle, const jlong id, const jstring property, const jint format) {
    const char* prop = env->GetStringUTFChars(property, nullptr);
    mpv_observe_property(reinterpret_cast<mpv_handle *>(handle), id, prop, static_cast<mpv_format>(format));
    env->ReleaseStringUTFChars(property, prop);
}

jni_func(void, unobserveProperty, const jlong handle, const uint64_t reply) {
    const int ret = mpv_unobserve_property(reinterpret_cast<mpv_handle *>(handle), reply);
    handleMpvError(env, ret);
}

jni_func(void, delProperty, const jlong handle, const jstring property) {
    const char* prop = env->GetStringUTFChars(property, nullptr);
    const auto err = mpv_del_property(reinterpret_cast<mpv_handle *>(handle), prop);
    env->ReleaseStringUTFChars(property, prop);
    handleMpvError(env, err);
}