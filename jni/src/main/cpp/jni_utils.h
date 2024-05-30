#pragma once

#include <jni.h>

#define jni_func_name(name) Java_dev_zt64_mpvkt_LibMpv_##name
#define jni_func(return_type, name, ...) JNIEXPORT return_type JNICALL jni_func_name(name) (JNIEnv *env, jobject obj, ##__VA_ARGS__)

bool acquire_jni_env(JavaVM *vm, JNIEnv **env);

void init_methods_cache(JNIEnv *env);

#define PRIMITIVE(name, value) \
extern jclass java_##name;     \
extern jmethodID java_##name##_init, java_##name##_##value;

PRIMITIVE(Integer, intValue)
PRIMITIVE(Double, doubleValue)
PRIMITIVE(Long, longValue)
PRIMITIVE(Boolean, booleanValue)

#undef PRIMITIVE

extern jclass mpv_MPVLib;
extern jmethodID mpv_MPVLib_eventProperty_S, mpv_MPVLib_eventProperty_Sb, mpv_MPVLib_eventProperty_Sl, mpv_MPVLib_eventProperty_SS, mpv_MPVLib_event, mpv_MPVLib_logMessage_SiS;
