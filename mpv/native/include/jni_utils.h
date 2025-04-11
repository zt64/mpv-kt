#pragma once

#include <jni.h>

#define jni_func_name(name) Java_dev_zt64_mpvkt_LibMpv_##name
#define jni_func(signature, name, ...) \
extern "C" JNIEXPORT signature JNICALL jni_func_name(name)(JNIEnv *env, jobject obj, ##__VA_ARGS__)

bool acquire_jni_env(JavaVM *vm, JNIEnv **env);

void init_methods_cache(JNIEnv *env);

void handleMpvError(JNIEnv *env, int error);

// Primitive types declaration macro
#define DECLARE_PRIMITIVE(name, value) \
inline jclass java_##name; \
inline jmethodID java_##name##_init, java_##name##_##value;

DECLARE_PRIMITIVE(Integer, intValue)
DECLARE_PRIMITIVE(Double, doubleValue)
DECLARE_PRIMITIVE(Long, longValue)
DECLARE_PRIMITIVE(Boolean, booleanValue)

// MpvNode classes declaration macro
#define DECLARE_NODE(name) \
inline jclass mpv_MpvNode##name; \
inline jmethodID mpv_MpvNode##name##_init;

// MpvEvent classes declaration macro
#define DECLARE_EVENT(name) \
inline jclass mpv_MpvEvent_##name; \
inline jmethodID mpv_MpvEvent_##name##_init;

// Exception class
inline jclass mpv_MPVException;
inline jmethodID mpv_MPVException_init;

// MpvNode base class
inline jclass mpv_MpvNode;
inline jmethodID mpv_MpvNode_init;

// Node types
DECLARE_NODE(String)
DECLARE_NODE(Flag)
DECLARE_NODE(Long)
DECLARE_NODE(Double)
DECLARE_NODE(Array)
DECLARE_NODE(Map)
DECLARE_NODE(Byte)

// API type and callbacks
inline jclass mpv_MpvRenderApiType;
inline jmethodID mpv_MpvRenderApiType_getOrdinal;

inline jclass mpv_MpvWakeupCallback;
inline jmethodID mpv_MpvWakeupCallback_invoke;

inline jclass mpv_MpvRenderUpdateCallback;
inline jmethodID mpv_MpvRenderUpdateCallback_invoke;

// Event types
DECLARE_EVENT(Shutdown)
DECLARE_EVENT(LogMessage)
DECLARE_EVENT(GetPropertyReply)
DECLARE_EVENT(SetPropertyReply)
DECLARE_EVENT(CommandReply)
DECLARE_EVENT(StartFile)
DECLARE_EVENT(EndFile)
DECLARE_EVENT(FileLoaded)
DECLARE_EVENT(ClientMessage)
DECLARE_EVENT(VideoReconfig)
DECLARE_EVENT(AudioReconfig)
DECLARE_EVENT(Seek)
DECLARE_EVENT(PlaybackRestart)
DECLARE_EVENT(PropertyChange)
DECLARE_EVENT(QueueOverflow)
DECLARE_EVENT(Hook)

// Map for node implementations
inline jclass java_Map;
inline jmethodID java_Map_init, java_Map_put;

inline jclass mpv_MpvLogLevel;
inline jmethodID mpv_MpvLogLevel_init, mpv_MpvLogLevel_getOrdinal;

#undef DECLARE_PRIMITIVE
#undef DECLARE_NODE
#undef DECLARE_EVENT