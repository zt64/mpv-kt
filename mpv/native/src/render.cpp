#include "jni_utils.h"
#include "mpv/render.h"

#include <cstdlib>

#include "render_gl.h"
#include <iostream>

typedef void* (*mpv_get_proc_address_fn)(void* ctx, const char* name);

jni_func(jlong, renderContextCreate, const jlong handle, const jobject apiType, const jobjectArray params) {
    const jsize len = env->GetArrayLength(params);
    auto* cparams = new mpv_render_param[len + 2]; // +2 for MPV_RENDER_PARAM_API_TYPE and MPV_RENDER_PARAM_INVALID
    const jint ordinal = env->CallIntMethod(apiType, mpv_MpvRenderApiType_getOrdinal);
    const char* apiTypeName = nullptr;

    switch (ordinal) {
        case 0:
            apiTypeName = MPV_RENDER_API_TYPE_OPENGL;
            break;
        case 1:
            apiTypeName = MPV_RENDER_API_TYPE_SW;
            break;
        default: break;
    }

    cparams[0] = {
        MPV_RENDER_PARAM_API_TYPE,
        const_cast<char *>(apiTypeName),
    };

    for (jsize i = 0; i < len; i++) {
        jobject obj = env->GetObjectArrayElement(params, i);
        jclass cls = env->GetObjectClass(obj);

        // not exposed as @JvmField, so we have to get the method for getType
        jfieldID typeField = env->GetFieldID(
            cls,
            "type",
            "Ldev/zt64/mpvkt/render/MpvRenderParamType;"
        );

        jobject typeEnumObj = env->GetObjectField(obj, typeField);
        if (typeEnumObj == nullptr) continue;

        // Get the ordinal from the enum
        jclass enumCls = env->GetObjectClass(typeEnumObj);
        jmethodID ordinalMethod = env->GetMethodID(enumCls, "ordinal", "()I");
        jint typeValue = env->CallIntMethod(typeEnumObj, ordinalMethod);

        if (typeValue == 0) { // OPENGL_INIT_PARAMS ordinal = 0
            jfieldID procAddressFid = env->GetFieldID(cls, "getProcAddress", "J");
            jlong procAddress = env->GetLongField(obj, procAddressFid);

            jfieldID addressCtxFid = env->GetFieldID(cls, "getProcAddressCtx", "J");
            jlong procAddressCtx = env->GetLongField(obj, addressCtxFid);

            mpv_opengl_init_params gl_init = {
                .get_proc_address = reinterpret_cast<mpv_get_proc_address_fn>(procAddress),
                .get_proc_address_ctx = reinterpret_cast<void*>(procAddressCtx),
            };

            cparams[i + 1] = {
                .type = MPV_RENDER_PARAM_OPENGL_INIT_PARAMS,
                .data = &gl_init,
            };
            // cparams[i + 1].type = MPV_RENDER_PARAM_OPENGL_INIT_PARAMS;
            // cparams[i + 1].data = &gl_init;
        } //else { // I've commented this out because it scares me.
        //     std::cout << "Shoudlnt run" << std::endl;
        //     fid = env->GetFieldID(cls, "data", "J");
        //     const jlong data = env->GetLongField(obj, fid);
        //
        //     cparams[i + 1].type = static_cast<mpv_render_param_type>(type);
        //     cparams[i + 1].data = reinterpret_cast<void *>(data);
        // }


        env->DeleteLocalRef(obj);
        env->DeleteLocalRef(cls);
        env->DeleteLocalRef(typeEnumObj);
        env->DeleteLocalRef(enumCls);
    }

    cparams[len + 1] = {
        MPV_RENDER_PARAM_INVALID,
        nullptr
    };

    mpv_render_context* res;
    const int result = mpv_render_context_create(&res, reinterpret_cast<mpv_handle *>(handle), cparams);
    handleMpvError(env, result);

    delete[] cparams;

    return reinterpret_cast<jlong>(res);
}

jni_func(int, renderContextSetParameter, const jlong ctx, jlong param, jobject value) {
    return 0;
}

jni_func(jobject, renderContextGetInfo, const jlong ctx, const jint param) {
    const auto param_ = mpv_render_param{
        .type = static_cast<mpv_render_param_type>(param),
        .data = nullptr
    };

    const int result = mpv_render_context_get_info(reinterpret_cast<mpv_render_context *>(ctx), param_);
    handleMpvError(env, result);

    jobject info = nullptr;
    switch (param_.type) {
        case MPV_RENDER_PARAM_API_TYPE:
        case MPV_RENDER_PARAM_SW_FORMAT:
            // Data is a string
            info = env->NewStringUTF(static_cast<const char *>(param_.data));
            break;

        case MPV_RENDER_PARAM_SW_SIZE:
        case MPV_RENDER_PARAM_SW_STRIDE:
            // Data is an integer or array
            info = env->NewIntArray(2);
            if (info != nullptr) {
                env->SetIntArrayRegion(reinterpret_cast<jintArray>(info), 0, 2, static_cast<const jint *>(param_.data));
            }
            break;

        case MPV_RENDER_PARAM_BLOCK_FOR_TARGET_TIME:
        case MPV_RENDER_PARAM_SKIP_RENDERING:
            // Data is a single integer
            info = env->NewObject(java_Integer, java_Integer_init, *static_cast<int *>(param_.data));
            break;

        default:
            // Unsupported type
            env->ThrowNew(mpv_MPVException, "Unsupported parameter type");
            return nullptr;
    }

    return info;
}

static jobject renderUpdateCallback = nullptr;

static void renderCallback(void* ctx) {
    auto* env = static_cast<JNIEnv *>(ctx);
    if (renderUpdateCallback != nullptr) env->CallVoidMethod(renderUpdateCallback, mpv_MpvRenderUpdateCallback_invoke);
}

jni_func(void, renderContextSetUpdateCallback, const jlong ctx, jobject callback) {
    if (renderUpdateCallback != nullptr) {
        env->DeleteGlobalRef(renderUpdateCallback);
        renderUpdateCallback = nullptr;
    }

    renderUpdateCallback = env->NewGlobalRef(callback);
    if (renderUpdateCallback == nullptr) return;

    mpv_render_context_set_update_callback(reinterpret_cast<mpv_render_context *>(ctx), renderCallback, env);
}

jni_func(jlong, renderContextUpdate, const jlong ctx) {
    return mpv_render_context_update(reinterpret_cast<mpv_render_context *>(ctx));
}

jni_func(int, renderContextRender, const jlong ctx, jobject type, jobjectArray params) {
    jclass cls = env->GetObjectClass(type); // Render Context Class
    jmethodID ordinalMethod = env->GetMethodID(cls, "ordinal", "()I");
    jint apiType = env->CallIntMethod(type, ordinalMethod);

    const char* apiTypeName = nullptr;

    switch (apiType) {
        case 0:
            apiTypeName = MPV_RENDER_API_TYPE_OPENGL;
            break;
        case 1:
            apiTypeName = MPV_RENDER_API_TYPE_SW;
            break;
        default: break;
    }


    const jsize len = env->GetArrayLength(params);
    auto* cparams = new mpv_render_param[len + 2];

    cparams[0] = {
        .type = MPV_RENDER_PARAM_API_TYPE,
        .data = (void*)(apiTypeName),
    };

    for (int i = 0; i < len; i++) {
        const jobject renderParamObj = env->GetObjectArrayElement(params, i);
        jclass renderParamCls = env->GetObjectClass(renderParamObj);


        jfieldID typeField = env->GetFieldID(
            renderParamCls,
            "type",
            "Ldev/zt64/mpvkt/render/MpvRenderParamType;"
        );

        jobject typeEnumObj = env->GetObjectField(renderParamObj, typeField);
        if (typeEnumObj == nullptr) continue;

        // Get the ordinal from the enum
        jclass enumCls = env->GetObjectClass(typeEnumObj);
        jmethodID typeOrdinal = env->GetMethodID(enumCls, "ordinal", "()I");
        jint typeValue = env->CallIntMethod(typeEnumObj, typeOrdinal);

        if (typeValue == 1) {
            auto fboFieldId = env->GetFieldID(renderParamCls, "fbo", "I");
            auto wFieldId = env->GetFieldID(renderParamCls, "w", "I");
            auto hFieldId = env->GetFieldID(renderParamCls, "h", "I");
            auto internalFormatFieldId = env->GetFieldID(renderParamCls, "internalFormat", "I");

            const jint fbo = env->GetIntField(renderParamObj, fboFieldId);
            const jint w = env->GetIntField(renderParamObj, wFieldId);
            const jint h = env->GetIntField(renderParamObj, hFieldId);
            const jint internalFormat = env->GetIntField(renderParamObj, internalFormatFieldId);

            mpv_opengl_fbo mpv_fbo = {
                .fbo =  fbo,
                .w = w,
                .h = h,
                .internal_format = internalFormat,
            };

            cparams[i + 1] = {
                .type = MPV_RENDER_PARAM_OPENGL_FBO,
                .data = &mpv_fbo,
            };
            continue;
        }

        if (typeValue == 2) { // flip y, if it exists at all, even if it
           auto flipYField = env->GetFieldID(renderParamCls, "flipY", "Z");
           auto flipY = env->GetBooleanField(renderParamObj, flipYField) ? 1 : 0;

           // laziness off the CHARTS!
            cparams[i + 1] = {
                .type = MPV_RENDER_PARAM_FLIP_Y,
                .data = &flipY,
            };
        }

        env->DeleteLocalRef(typeEnumObj);
        env->DeleteLocalRef(enumCls);
        env->DeleteLocalRef(renderParamObj);
        env->DeleteLocalRef(renderParamCls);
    }
    env->DeleteLocalRef(cls);

    cparams[len + 1] = {
        .type = MPV_RENDER_PARAM_INVALID,
        .data = nullptr,
    };

    auto* mpvc = reinterpret_cast<mpv_render_context*>(ctx);

    mpv_render_param* cparams_ptr = cparams;
    const int result = mpv_render_context_render(mpvc, cparams_ptr);
    handleMpvError(env, result);
    mpv_render_context_report_swap(mpvc);

    delete[] cparams;
    return 0;
}

jni_func(jbyteArray, renderContextRenderSw, const jlong ctx, const jlongArray params) {
    int w = 100;
    int h = 100;
    // Allocate a buffer for the pixel data
    size_t pitch = w * 4; // Assuming 4 bytes per pixel (e.g., 32-bit RGBA)
    void* pixels = malloc(pitch * h);
    if (pixels == nullptr) {
        return nullptr; // Memory allocation failed
    }

    int sw_size[] = {w, h};
    // Set up mpv render parameters
    mpv_render_param params2[] = {
        {MPV_RENDER_PARAM_SW_SIZE, sw_size},
        {MPV_RENDER_PARAM_SW_FORMAT, reinterpret_cast<void *>(*"0bgr")},
        {MPV_RENDER_PARAM_SW_STRIDE, &pitch},
        {MPV_RENDER_PARAM_SW_POINTER, pixels},
        // {0}
    };

    // Call the render function
    if (const int result = mpv_render_context_render(reinterpret_cast<mpv_render_context *>(ctx), params2);
        result < 0) {
        free(pixels); // Free allocated memory on failure
        return nullptr;
    }

    // Copy the pixel data into a ByteArray to send to the JVM
    const size_t totalSize = pitch * h;
    jbyteArray byteArray = env->NewByteArray(static_cast<int>(totalSize));
    if (byteArray == nullptr) {
        free(pixels); // Out of memory error thrown
        return nullptr;
    }

    env->SetByteArrayRegion(byteArray, 0, totalSize, static_cast<jbyte *>(pixels));

    // Free the memory used by the pixels buffer
    free(pixels);

    return byteArray;
}

jni_func(void, renderContextReportSwap, const jlong ctx) {
    mpv_render_context_report_swap(reinterpret_cast<mpv_render_context *>(ctx));
}

jni_func(void, renderContextFree, const jlong ctx) {
    mpv_render_context_free(reinterpret_cast<mpv_render_context *>(ctx));
}

static jobject surface;

#ifdef __android__
jni_func(void, attachSurface, jlong handle, jobject surface_) {
    surface = env->NewGlobalRef(surface_);
    auto wid = reinterpret_cast<intptr_t>(surface);
    mpv_set_option(reinterpret_cast<mpv_handle *>(handle), "wid", MPV_FORMAT_INT64, &wid);
}

jni_func(void, detachSurface, jlong handle) {
    int64_t wid = 0;
    mpv_set_option(reinterpret_cast<mpv_handle *>(handle), "wid", MPV_FORMAT_INT64, &wid);

    env->DeleteGlobalRef(surface);
    surface = nullptr;
}
#endif