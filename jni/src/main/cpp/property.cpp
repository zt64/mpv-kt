#include <bits/stdint-uintn.h>
#include <mpv/client.h>
#include "jni_utils.h"

extern "C" {
jni_func(jint, setOptionString, jlong handle, jstring option, jstring value);

jni_func(jobject, getPropertyLong, jlong handle, jstring property);
jni_func(void, setPropertyLong, jlong handle, jstring property, jlong value);
jni_func(jobject, getPropertyDouble, jlong handle, jstring property);
jni_func(void, setPropertyDouble, jlong handle, jstring property, jdouble value);
jni_func(jobject, getPropertyBoolean, jlong handle, jstring property);
jni_func(void, setPropertyBoolean, jlong handle, jstring property, jboolean value);
jni_func(jstring, getPropertyString, jlong handle, jstring jproperty);
jni_func(void, setPropertyString, jlong handle, jstring jproperty, jstring jvalue);

jni_func(void, observeProperty, jlong handle, jstring property, jint format);
jni_func(void, unobserveProperty, jlong handle, jlong reply);

jni_func(void, delProperty, jlong handle, jstring property);
}

static int common_get_property(JNIEnv *env, jlong handle, jstring jproperty, mpv_format format, void *output) {
    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    const int result = mpv_get_property(reinterpret_cast<mpv_handle *>(handle), prop, format, output);
//    if (result < 0)
//        ALOGE("mpv_get_property(%s) format %d returned error %s", prop, format, mpv_error_string(result));
    env->ReleaseStringUTFChars(jproperty, prop);

    return result;
}

static int common_set_property(JNIEnv *env, jlong handle, jstring jproperty, mpv_format format, void *value) {
    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    const int result = mpv_set_property(reinterpret_cast<mpv_handle *>(handle), prop, format, value);
//    if (result < 0) {
//        ALOGE("mpv_set_property(%s, %p) format %d returned error %s", prop, value, format, mpv_error_string(result));
//    }
    env->ReleaseStringUTFChars(jproperty, prop);

    return result;
}

jni_func(jobject, getPropertyLong, jlong handle, jstring jproperty) {
    int64_t value = 0;
    common_get_property(env, handle, jproperty, MPV_FORMAT_INT64, &value);
    if (common_get_property(env, handle, jproperty, MPV_FORMAT_INT64, &value) < 0)
        return nullptr;
//    return (jlong) value;
    return env->NewObject(java_Long, java_Long_init, (jlong) value);
}

jni_func(void, setPropertyLong, jlong handle, jstring jproperty, jlong value) {
    common_set_property(env, handle, jproperty, MPV_FORMAT_INT64, (int64_t *) &value);
}

jni_func(jobject, getPropertyDouble, jlong handle, jstring jproperty) {
    double value = 0;
    if (common_get_property(env, handle, jproperty, MPV_FORMAT_DOUBLE, &value) < 0)
        return nullptr;
    return env->NewObject(java_Double, java_Double_init, (jdouble) value);
}

jni_func(void, setPropertyDouble, jlong handle, jstring jproperty, jdouble value) {
    common_set_property(env, handle, jproperty, MPV_FORMAT_DOUBLE, &value);
}

jni_func(jobject, getPropertyBoolean, jlong handle, jstring jproperty) {
    int value = 0;
    if (common_get_property(env, handle, jproperty, MPV_FORMAT_FLAG, &value) < 0)
        return nullptr;
    return env->NewObject(java_Boolean, java_Boolean_init, (jboolean) value);
}

jni_func(void, setPropertyBoolean, jlong handle, jstring jproperty, jboolean value) {
    common_set_property(env, handle, jproperty, MPV_FORMAT_FLAG, &value);
}

jni_func(jstring, getPropertyString, jlong handle, jstring jproperty) {
    char *value = nullptr;
    if (common_get_property(env, handle, jproperty, MPV_FORMAT_STRING, &value) < 0)
        return nullptr;
    const jstring &ret = env->NewStringUTF(value);
    mpv_free(value);
    return ret;
}

jni_func(void, setPropertyString, jlong handle, jstring jproperty, jstring jvalue) {
    const char *value = env->GetStringUTFChars(jvalue, nullptr);
    common_set_property(env, handle, jproperty, MPV_FORMAT_STRING, &value);
    env->ReleaseStringUTFChars(jvalue, value);
}

jni_func(void, observeProperty, jlong handle, jstring property, jint format) {
    const char *prop = env->GetStringUTFChars(property, nullptr);
    mpv_observe_property(reinterpret_cast<mpv_handle *>(handle), 0, prop, (mpv_format) format);
    env->ReleaseStringUTFChars(property, prop);
}

jni_func(void, unobserveProperty, jlong handle, uint64_t reply) {
    if (mpv_unobserve_property(reinterpret_cast<mpv_handle *>(handle), reply) < 0) {
        env->ExceptionOccurred();
    }
}

jni_func(void, delProperty, jlong handle, jstring property) {
    const char *prop = env->GetStringUTFChars(property, nullptr);
    const auto err = mpv_del_property(reinterpret_cast<mpv_handle *>(handle), prop);
    env->ReleaseStringUTFChars(property, prop);
}
