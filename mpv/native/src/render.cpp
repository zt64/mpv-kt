#include "jni_utils.h"
#include "mpv/render.h"

#include <cstdlib>

#include "render_gl.h"
#include <iostream>

typedef void * (*mpv_get_proc_address_fn)(void *ctx, const char *name);

int getRenderParamType(JNIEnv *env, jobject param, jclass paramCls) {
    jfieldID typeField = env->GetFieldID(
        paramCls,
        "type",
        "Ldev/zt64/mpvkt/render/MpvRenderParamType;"
    );

    jobject typeEnumObj = env->GetObjectField(param, typeField);
    if (typeEnumObj == nullptr) return -1;

    // Get the ordinal from the enum
    jclass enumCls = env->GetObjectClass(typeEnumObj);
    jmethodID ordinalMethod = env->GetMethodID(enumCls, "ordinal", "()I");
    jint typeValue = env->CallIntMethod(typeEnumObj, ordinalMethod);

    env->DeleteLocalRef(typeEnumObj);
    env->DeleteLocalRef(enumCls);

    return typeValue;
}

const char *getRenderParamAPIType(JNIEnv *env, jobject apiType) {
    const jint ordinal = env->CallIntMethod(apiType, mpv_MpvRenderApiType_getOrdinal);
    const char *apiTypeName = nullptr;

    switch (ordinal) {
        case 0:
            apiTypeName = MPV_RENDER_API_TYPE_OPENGL;
            break;
        case 1:
            apiTypeName = MPV_RENDER_API_TYPE_SW;
            break;
        default: break;
    }

    return apiTypeName;
}

jni_func(jlong, renderContextCreate, const jlong handle, const jobject apiType, const jobjectArray params) {
    const jsize len = env->GetArrayLength(params);
    auto *cparams = new mpv_render_param[len + 2];
    cparams[0] = {
        MPV_RENDER_PARAM_API_TYPE,
        const_cast<char *>(getRenderParamAPIType(env, apiType)),
    };


    for (jsize i = 0; i < len; i++) {
        jobject obj = env->GetObjectArrayElement(params, i);
        jclass cls = env->GetObjectClass(obj);
        auto type = static_cast<mpv_render_param_type>(getRenderParamType(env, obj, cls));

        cparams[i + 1].type = type;
        switch (type) {
            case MPV_RENDER_PARAM_OPENGL_INIT_PARAMS: {
                jfieldID procAddressFid = env->GetFieldID(cls, "getProcAddress", "J");
                jlong procAddress = env->GetLongField(obj, procAddressFid);

                jfieldID addressCtxFid = env->GetFieldID(cls, "getProcAddressCtx", "J");
                jlong procAddressCtx = env->GetLongField(obj, addressCtxFid);

                mpv_opengl_init_params gl_init = {
                    .get_proc_address = reinterpret_cast<mpv_get_proc_address_fn>(procAddress),
                    .get_proc_address_ctx = reinterpret_cast<void *>(procAddressCtx),
                };

                cparams[i + 1].data = &gl_init;
                break;
            }

            case MPV_RENDER_PARAM_OPENGL_FBO: {
                const auto fboFieldId = env->GetFieldID(cls, "fbo", "I");
                const auto wFieldId = env->GetFieldID(cls, "w", "I");
                const auto hFieldId = env->GetFieldID(cls, "h", "I");
                const auto internalFormatFieldId = env->GetFieldID(cls, "internalFormat", "I");

                const jint fbo = env->GetIntField(obj, fboFieldId);
                const jint w = env->GetIntField(obj, wFieldId);
                const jint h = env->GetIntField(obj, hFieldId);
                const jint internalFormat = env->GetIntField(obj, internalFormatFieldId);

                mpv_opengl_fbo mpv_fbo = {
                    .fbo = fbo,
                    .w = w,
                    .h = h,
                    .internal_format = internalFormat,
                };

                cparams[i + 1].data = &mpv_fbo;
                break;
            }
            // long
            case MPV_RENDER_PARAM_SW_POINTER: {
                const auto data = env->GetFieldID(cls, "data", "Ljava/lang/Object;");
                long longValue = env->GetLongField(obj, data);

                cparams[i + 1].data = &longValue;
                break;
            }
            // int
            case MPV_RENDER_PARAM_DEPTH:
            case MPV_RENDER_PARAM_DRM_DRAW_SURFACE_SIZE:
            case MPV_RENDER_PARAM_SW_SIZE:
            case MPV_RENDER_PARAM_SW_FORMAT:
            case MPV_RENDER_PARAM_SW_STRIDE: {
                const auto data = env->GetFieldID(cls, "data", "Ljava/lang/Object;");
                int integer = env->GetIntField(obj, data);

                cparams[i + 1].data = &integer;
                break;
            }
            // bool
            case MPV_RENDER_PARAM_FLIP_Y:
            case MPV_RENDER_PARAM_ADVANCED_CONTROL:
            case MPV_RENDER_PARAM_BLOCK_FOR_TARGET_TIME:
            case MPV_RENDER_PARAM_SKIP_RENDERING: {
                const auto data = env->GetFieldID(cls, "data", "Ljava/lang/Object;");
                bool boolean = env->GetBooleanField(obj, data);

                cparams[i + 1].data = &boolean;
                break;
            }
            // float
            case MPV_RENDER_PARAM_AMBIENT_LIGHT: {
                const auto data = env->GetFieldID(cls, "data", "Ljava/lang/Object;");
                float ambientLight = env->GetFloatField(obj, data);

                cparams[i + 1].data = &ambientLight;
                break;
            }
            // string
            case MPV_RENDER_PARAM_ICC_PROFILE:
            case MPV_RENDER_PARAM_X11_DISPLAY:
            case MPV_RENDER_PARAM_WL_DISPLAY:
            case MPV_RENDER_PARAM_DRM_DISPLAY:
            case MPV_RENDER_PARAM_DRM_DISPLAY_V2: {
                const auto data = env->GetFieldID(cls, "data", "Ljava/lang/String;");
                auto jStringParam = reinterpret_cast<jstring>(env->GetObjectField(obj, data));
                auto cstringParam = env->GetStringUTFChars(jStringParam, reinterpret_cast<jboolean *>(true));
                cparams[i + 1].data = &cstringParam;
                env->ReleaseStringUTFChars(jStringParam, cstringParam);
                break;
            }

            default:
                env->ThrowNew(mpv_MPVException, "Unsupported parameter type");
        }


        env->DeleteLocalRef(obj);
        env->DeleteLocalRef(cls);
    }


    cparams[len + 1] = {
        MPV_RENDER_PARAM_INVALID,
        nullptr
    };
    mpv_render_context *res;
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

static void renderCallback(void *ctx) {
    auto *env = static_cast<JNIEnv *>(ctx);
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
    const jsize len = env->GetArrayLength(params);
    auto *cparams = new mpv_render_param[len + 2];

    cparams[0] = {
        MPV_RENDER_PARAM_API_TYPE,
        const_cast<char *>(getRenderParamAPIType(env, type)),
    };

    for (jsize i = 0; i < len; i++) {
        jobject obj = env->GetObjectArrayElement(params, i);
        jclass cls = env->GetObjectClass(obj);
        auto type = static_cast<mpv_render_param_type>(getRenderParamType(env, obj, cls));

        cparams[i + 1].type = type;
        switch (type) {
            case MPV_RENDER_PARAM_OPENGL_INIT_PARAMS: {
                jfieldID procAddressFid = env->GetFieldID(cls, "getProcAddress", "J");
                jlong procAddress = env->GetLongField(obj, procAddressFid);

                jfieldID addressCtxFid = env->GetFieldID(cls, "getProcAddressCtx", "J");
                jlong procAddressCtx = env->GetLongField(obj, addressCtxFid);

                mpv_opengl_init_params gl_init = {
                    .get_proc_address = reinterpret_cast<mpv_get_proc_address_fn>(procAddress),
                    .get_proc_address_ctx = reinterpret_cast<void *>(procAddressCtx),
                };

                cparams[i + 1].data = &gl_init;
                break;
            }

            case MPV_RENDER_PARAM_OPENGL_FBO: {
                const auto fboFieldId = env->GetFieldID(cls, "fbo", "I");
                const auto wFieldId = env->GetFieldID(cls, "w", "I");
                const auto hFieldId = env->GetFieldID(cls, "h", "I");
                const auto internalFormatFieldId = env->GetFieldID(cls, "internalFormat", "I");

                const jint fbo = env->GetIntField(obj, fboFieldId);
                const jint w = env->GetIntField(obj, wFieldId);
                const jint h = env->GetIntField(obj, hFieldId);
                const jint internalFormat = env->GetIntField(obj, internalFormatFieldId);

                mpv_opengl_fbo mpv_fbo = {
                    .fbo = fbo,
                    .w = w,
                    .h = h,
                    .internal_format = internalFormat,
                };

                cparams[i + 1].data = &mpv_fbo;
                break;
            }
            // long
            case MPV_RENDER_PARAM_SW_POINTER: {
                const auto data = env->GetFieldID(cls, "data", "Ljava/lang/Object;");
                long longValue = env->GetLongField(obj, data);

                cparams[i + 1].data = &longValue;
                break;
            }
            // int
            case MPV_RENDER_PARAM_DEPTH:
            case MPV_RENDER_PARAM_DRM_DRAW_SURFACE_SIZE:
            case MPV_RENDER_PARAM_SW_SIZE:
            case MPV_RENDER_PARAM_SW_FORMAT:
            case MPV_RENDER_PARAM_SW_STRIDE: {
                const auto data = env->GetFieldID(cls, "data", "Ljava/lang/Object;");
                int integer = env->GetIntField(obj, data);

                cparams[i + 1].data = &integer;
                break;
            }
            // bool
            case MPV_RENDER_PARAM_FLIP_Y:
            case MPV_RENDER_PARAM_ADVANCED_CONTROL:
            case MPV_RENDER_PARAM_BLOCK_FOR_TARGET_TIME:
            case MPV_RENDER_PARAM_SKIP_RENDERING: {
                const auto data = env->GetFieldID(cls, "data", "Ljava/lang/Object;");
                bool boolean = env->GetBooleanField(obj, data);

                cparams[i + 1].data = &boolean;
                break;
            }
            // float
            case MPV_RENDER_PARAM_AMBIENT_LIGHT: {
                const auto data = env->GetFieldID(cls, "data", "Ljava/lang/Object;");
                float ambientLight = env->GetFloatField(obj, data);

                cparams[i + 1].data = &ambientLight;
                break;
            }
            // string
            case MPV_RENDER_PARAM_ICC_PROFILE:
            case MPV_RENDER_PARAM_X11_DISPLAY:
            case MPV_RENDER_PARAM_WL_DISPLAY:
            case MPV_RENDER_PARAM_DRM_DISPLAY:
            case MPV_RENDER_PARAM_DRM_DISPLAY_V2: {
                const auto data = env->GetFieldID(cls, "data", "Ljava/lang/String;");
                auto jStringParam = reinterpret_cast<jstring>(env->GetObjectField(obj, data));
                auto cstringParam = env->GetStringUTFChars(jStringParam, reinterpret_cast<jboolean *>(true));
                cparams[i + 1].data = &cstringParam;
                env->ReleaseStringUTFChars(jStringParam, cstringParam);
                break;
            }

            default:
                env->ThrowNew(mpv_MPVException, "Unsupported parameter type");
        }


        env->DeleteLocalRef(obj);
        env->DeleteLocalRef(cls);
    }


    cparams[len + 1] = {
        MPV_RENDER_PARAM_INVALID,
        nullptr
    };
    auto *mpvc = reinterpret_cast<mpv_render_context *>(ctx);

    mpv_render_param *cparams_ptr = cparams;
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
    void *pixels = malloc(pitch * h);
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
