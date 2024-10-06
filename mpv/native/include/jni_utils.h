#pragma once

#include <jni.h>

#define jni_func_name(name) Java_dev_zt64_mpvkt_LibMpv_##name
#define jni_func(return_type, name, ...) JNIEXPORT return_type JNICALL jni_func_name(name) (JNIEnv *env, jobject obj, ##__VA_ARGS__)

bool acquire_jni_env(JavaVM *vm, JNIEnv **env);

void init_methods_cache(JNIEnv *env);

void handleMpvError(JNIEnv *env, int error);

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

inline jclass mpv_MPVException;
inline jmethodID mpv_MPVException_init;

// class MpvNode
inline jclass mpv_MpvNode;
inline jmethodID mpv_MpvNode_init;

inline jclass mpv_MpvNodeString, mpv_MpvNodeFlag, mpv_MpvNodeLong, mpv_MpvNodeDouble, mpv_MpvNodeArray, mpv_MpvNodeMap, mpv_MpvNodeByte;
inline jmethodID mpv_MpvNodeString_init, mpv_MpvNodeFlag_init, mpv_MpvNodeLong_init, mpv_MpvNodeDouble_init, mpv_MpvNodeArray_init, mpv_MpvNodeMap_init, mpv_MpvNodeByte_init;

inline jclass java_Map;
inline jmethodID java_Map_init, java_Map_put;
