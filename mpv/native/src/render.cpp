#include "jni_utils.h"
#include "mpv/render.h"

#include <cstdlib>

jni_func(jlong, renderContextCreate, const jlong handle, const jobject apiType, const jobjectArray params) {
    const jsize len = env->GetArrayLength(params);
    auto* cparams = new mpv_render_param[len + 2]; // +2 for MPV_RENDER_PARAM_API_TYPE and MPV_RENDER_PARAM_INVALID

    const jint ordinal = env->CallIntMethod(apiType, mpv_MpvRenderApiType_getOrdinal);
    const char* apiTypeName = nullptr;

    switch (ordinal) {
        case 0:
            apiTypeName = "opengl";
            break;
        case 1:
            apiTypeName = "sw";
            break;
        default: break;
    }

    cparams[0] = {
        MPV_RENDER_PARAM_API_TYPE,
        const_cast<char *>(apiTypeName),
    };

    for (int i = 0; i < len; i++) {
        jobject obj = env->GetObjectArrayElement(params, i);
        jclass cls = env->GetObjectClass(obj);

        jfieldID fid = env->GetFieldID(cls, "type", "I");
        jint type = env->GetIntField(obj, fid);

        fid = env->GetFieldID(cls, "data", "J");
        const jlong data = env->GetLongField(obj, fid);

        cparams[i + 1].type = static_cast<mpv_render_param_type>(type);
        cparams[i + 1].data = reinterpret_cast<void *>(data);

        env->DeleteLocalRef(obj);
        env->DeleteLocalRef(cls);
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

jni_func(int, renderContextSetParameter, const jlong ctx, jlong param) {
    return 0;
}

jni_func(jobject, renderContextGetInfo, const jlong ctx, const jint param) {
    printf("Param: %d\n", param);
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

jni_func(uint64_t, renderContextUpdate, const jlong ctx) {
    return mpv_render_context_update(reinterpret_cast<mpv_render_context *>(ctx));
}

jni_func(int, renderContextRender, const jlong ctx, jlongArray params) {
    const jsize len = env->GetArrayLength(params);
    auto* cparams = new mpv_render_param[len];

    for (int i = 0; i < len; i++) {
        const jlong data = env->GetLongArrayElements(params, nullptr)[i];
        cparams[i].data = reinterpret_cast<void *>(data);
    }

    mpv_render_param* cparams_ptr = cparams;
    mpv_render_context_render(reinterpret_cast<mpv_render_context *>(ctx), cparams_ptr);

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