#include <mpv/client.h>
#include "jni_utils.h"

extern "C" {
#define PROPERTY(name, value) \
    jni_func(value, getProperty##name, jlong, jstring); \
    jni_func(void, setProperty##name, jlong, jstring, value);

jni_func(jint, setOption, jlong handle, jstring option, jstring value);

jni_func(jobject, getPropertyLong, jlong handle, jstring property);
jni_func(void, setPropertyLong, jlong handle, jstring property, jlong value);
jni_func(jobject, getPropertyDouble, jlong handle, jstring property);
jni_func(void, setPropertyDouble, jlong handle, jstring property, jdouble value);
jni_func(jobject, getPropertyFlag, jlong handle, jstring property);
jni_func(void, setPropertyFlag, jlong handle, jstring property, jboolean value);
jni_func(jstring, getPropertyString, jlong handle, jstring property);
jni_func(void, setPropertyString, jlong handle, jstring property, jstring jvalue);
jni_func(jobjectArray, getPropertyArray, jlong handle, jstring property);
jni_func(jobject, getPropertyMap, jlong handle, jstring property);

jni_func(void, observeProperty, jlong handle, jstring property, jint format);
jni_func(void, unobserveProperty, jlong handle, jlong reply);

jni_func(void, delProperty, jlong handle, jstring property);

#undef PROPERTY
}

jni_func(jint, setOption, jlong handle, jstring option, jstring value) {
    const char *opt = env->GetStringUTFChars(option, nullptr);
    const char *val = env->GetStringUTFChars(value, nullptr);

    const int ret = mpv_set_option_string(reinterpret_cast<mpv_handle *>(handle), opt, val);

    env->ReleaseStringUTFChars(option, opt);
    env->ReleaseStringUTFChars(value, val);

    return ret;
}

static void common_get_property(JNIEnv *env, const jlong handle, jstring property, mpv_format format, void *output) {
    const char *prop = env->GetStringUTFChars(property, nullptr);
    const int result = mpv_get_property(reinterpret_cast<mpv_handle *>(handle), prop, format, output);
    env->ReleaseStringUTFChars(property, prop);

    handleMpvError(env, result);
}

static int common_set_property(JNIEnv *env, const jlong handle, jstring property, mpv_format format, void *value) {
    const char *prop = env->GetStringUTFChars(property, nullptr);
    const int result = mpv_set_property(reinterpret_cast<mpv_handle *>(handle), prop, format, value);
    env->ReleaseStringUTFChars(property, prop);

    return result;
}

jobject initJavaLong(JNIEnv *env, jlong value) {
    return env->NewObject(java_Long, java_Long_init, value);
}

jni_func(jobject, getPropertyLong, jlong handle, jstring property) {
    int64_t value = 0;
    common_get_property(env, handle, property, MPV_FORMAT_INT64, &value);
    return env->NewObject(java_Long, java_Long_init, value);
}

jni_func(void, setPropertyLong, jlong handle, jstring property, jlong value) {
    common_set_property(env, handle, property, MPV_FORMAT_INT64, &value);
}

jobject initJavaDouble(JNIEnv *env, jdouble value) {
    return env->NewObject(java_Double, java_Double_init, value);
}

jni_func(jobject, getPropertyDouble, jlong handle, jstring property) {
    double value = 0;
    common_get_property(env, handle, property, MPV_FORMAT_DOUBLE, &value);
    return env->NewObject(java_Double, java_Double_init, value);
}

jni_func(void, setPropertyDouble, jlong handle, jstring property, jdouble value) {
    common_set_property(env, handle, property, MPV_FORMAT_DOUBLE, &value);
}

jobject initJavaBoolean(JNIEnv *env, jboolean value) {
    return env->NewObject(java_Boolean, java_Boolean_init, value);
}

jni_func(jobject, getPropertyFlag, jlong handle, jstring property) {
    int value = 0;
    common_get_property(env, handle, property, MPV_FORMAT_FLAG, &value);
    return env->NewObject(java_Boolean, java_Boolean_init, (jboolean) value);
}

jni_func(void, setPropertyFlag, jlong handle, jstring property, jboolean value) {
    common_set_property(env, handle, property, MPV_FORMAT_FLAG, &value);
}

jni_func(jstring, getPropertyString, jlong handle, jstring property) {
    char *value = nullptr;
    common_get_property(env, handle, property, MPV_FORMAT_STRING, &value);
    const jstring &ret = env->NewStringUTF(value);
    mpv_free(value);
    return ret;
}

jni_func(void, setPropertyString, jlong handle, jstring property, jstring jvalue) {
    const char *value = env->GetStringUTFChars(jvalue, nullptr);
    common_set_property(env, handle, property, MPV_FORMAT_STRING, &value);
    env->ReleaseStringUTFChars(jvalue, value);
}

jobject nodeToJobject(JNIEnv *env, mpv_node node);

jobjectArray arrayToJvm(JNIEnv *env, const mpv_node value) {
    const mpv_node_list *list = value.u.list;
    jobjectArray objArr = env->NewObjectArray(list->num, mpv_MpvNode, nullptr);

    for (int i = 0; i < list->num; i++) {
        const mpv_node node = list->values[i];
        jobject a = nodeToJobject(env, node);

        env->SetObjectArrayElement(objArr, i, a);
    }

    return objArr;
}

jobject mapToJvm(JNIEnv *env, const mpv_node node) {
    // Create a local reference frame for 256 references
    env->PushLocalFrame(256);

    const mpv_node_list *list = node.u.list;

    // Create the Java HashMap
    const jobject hashMap = env->NewObject(java_Map, java_Map_init);

    printf("Size: %d", list->num);
    // Loop through the mpv_node_list
    for (int i = 0; i < list->num; i++) {
        printf("Here %i", i);
        const char* keyStr = list->keys[i - 0];
        if (keyStr == nullptr) continue;  // Ensure the key is valid

        printf("Key: %s", keyStr);
        // Convert key to jstring
        const jstring key = env->NewStringUTF(keyStr);

        // Convert value to Java object
        const jobject value = nodeToJobject(env, list->values[i]);

        // Insert into HashMap
        env->CallObjectMethod(hashMap, java_Map_put, key, value);

        // Clean up local references for key and value
        env->DeleteLocalRef(key);
        env->DeleteLocalRef(value);
    }

    // Pop the local frame, return the HashMap as a local reference
    return env->PopLocalFrame(hashMap);
}

jni_func(jobjectArray, getPropertyArray, const jlong handle, jstring property) {
    mpv_node value;
    common_get_property(env, handle, property, MPV_FORMAT_NODE, &value);
    return arrayToJvm(env, value);
}

jni_func(jobject, getPropertyMap, const jlong handle, jstring property) {
    mpv_node value;
    common_get_property(env, handle, property, MPV_FORMAT_NODE, &value);
    return mapToJvm(env, value);
}

jni_func(void, observeProperty, jlong handle, jstring property, jint format) {
    const char *prop = env->GetStringUTFChars(property, nullptr);
    mpv_observe_property(reinterpret_cast<mpv_handle *>(handle), 0, prop, static_cast<mpv_format>(format));
    env->ReleaseStringUTFChars(property, prop);
}

jni_func(void, unobserveProperty, const jlong handle, const uint64_t reply) {
    const int ret = mpv_unobserve_property(reinterpret_cast<mpv_handle *>(handle), reply);
    handleMpvError(env, ret);
}

jni_func(void, delProperty, jlong handle, jstring property) {
    const char *prop = env->GetStringUTFChars(property, nullptr);
    const auto err = mpv_del_property(reinterpret_cast<mpv_handle *>(handle), prop);
    env->ReleaseStringUTFChars(property, prop);
    handleMpvError(env, err);
}

jobject nodeToJobject(JNIEnv *env, mpv_node node) {
    switch (node.format) {
        case MPV_FORMAT_OSD_STRING:
        case MPV_FORMAT_STRING: {
            // Convert C string to jstring
            jstring str = env->NewStringUTF(node.u.string);

            // Wrap the string in a custom Java object
            jobject obj = env->NewObject(mpv_MpvNodeString, mpv_MpvNodeString_init, str);
            env->DeleteLocalRef(str);
            return obj;
        }
        case MPV_FORMAT_FLAG:
            return initJavaBoolean(env, node.u.flag);
        case MPV_FORMAT_INT64:
            return initJavaLong(env, node.u.int64);
        case MPV_FORMAT_DOUBLE:
            return initJavaDouble(env, node.u.double_);
        case MPV_FORMAT_NODE_ARRAY:
            return arrayToJvm(env, node);
        case MPV_FORMAT_NODE_MAP:
            return mapToJvm(env, node);
        case MPV_FORMAT_BYTE_ARRAY: {
            const mpv_byte_array *ba = node.u.ba;

            const jbyteArray jba = env->NewByteArray(ba->size);
            if (jba == nullptr) {
                // Handle out-of-memory error or array allocation failure
                return nullptr;
            }

            // Copy the native byte array data into the jbyteArray
            env->SetByteArrayRegion(jba, 0, ba->size, static_cast<const jbyte *>(ba->data));

            // Return the jbyteArray as a jobject
            return jba;
        }
        default:
            break;
    }
    return nullptr;
}