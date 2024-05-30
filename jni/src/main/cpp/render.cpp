#include "jni_utils.h"
#include <mpv/render.h>

extern "C" {
jni_func(jlong, renderContextCreate, jlong handle, jobjectArray params);
jni_func(int, renderContextSetParameter, jlong ctx, jlong param);
jni_func(int, renderContextGetInfo, jlong ctx, jlong param);
jni_func(void, renderContextSetUpdateCallback, jlong ctx, jobject callback);
jni_func(uint64_t, renderContextUpdate, jlong ctx);
jni_func(int, renderContextRender, jlong ctx, jlongArray params);
jni_func(void, renderContextReportSwap, jlong ctx);
jni_func(void, renderContextFree, jlong ctx);
jni_func(void, attachSurface, jlong handle, jobject surface_);
jni_func(void, detachSurface, jlong handle);
}

jni_func(jlong, renderContextCreate, jlong handle, jobjectArray params) {
    mpv_render_param *cparams = nullptr;

    if (params) {
        jsize len = env->GetArrayLength(params);
        cparams = new mpv_render_param[len];

        for (int i = 0; i < len; i++) {
            jobject obj = env->GetObjectArrayElement(params, i);
            jclass cls = env->GetObjectClass(obj);
            jfieldID fid = env->GetFieldID(cls, "type", "I");
            jint type = env->GetIntField(obj, fid);

            fid = env->GetFieldID(cls, "data", "J");
            jlong data = env->GetLongField(obj, fid);

            cparams[i].type = static_cast<mpv_render_param_type>(type);
            cparams[i].data = reinterpret_cast<void *>(data);
        }
    }

    mpv_render_context *res;

    mpv_render_context_create(&res, reinterpret_cast<mpv_handle *>(handle), cparams);

    return reinterpret_cast<jlong>(res);
}

jni_func(int, renderContextSetParameter, jlong ctx, jlong param) {
    return 0;
}

jni_func(int, renderContextGetInfo, jlong ctx, jlong param) {
    auto param_ = mpv_render_param{
            .type = MPV_RENDER_PARAM_INVALID,
            .data = nullptr
    };
    mpv_render_context_get_info(reinterpret_cast<mpv_render_context *>(ctx), param_);
    return 0;
}

jni_func(void, renderContextSetUpdateCallback, jlong ctx, jobject callback) {
    mpv_render_update_fn fn = [](void *ctx) {

    };

    mpv_render_context_set_update_callback(reinterpret_cast<mpv_render_context *>(ctx), fn, nullptr);
}

jni_func(uint64_t, renderContextUpdate, jlong ctx) {
    return mpv_render_context_update(reinterpret_cast<mpv_render_context *>(ctx));
}

jni_func(int, renderContextRender, jlong ctx, jlongArray params) {
    const jsize len = env->GetArrayLength(params);
    auto *cparams = new mpv_render_param[len];

    for (int i = 0; i < len; i++) {
        jlong data = env->GetLongArrayElements(params, nullptr)[i];
        cparams[i].data = reinterpret_cast<void *>(data);
    }

    mpv_render_param *cparams_ptr = cparams;
    mpv_render_context_render(reinterpret_cast<mpv_render_context *>(ctx), cparams_ptr);

    delete[] cparams;

    return 0;
}

jni_func(void, renderContextReportSwap, jlong ctx) {
    mpv_render_context_report_swap(reinterpret_cast<mpv_render_context *>(ctx));
}

jni_func(void, renderContextFree, jlong ctx) {
    mpv_render_context_free(reinterpret_cast<mpv_render_context *>(ctx));
}

static jobject surface;

jni_func(void, attachSurface, jlong handle, jobject surface_) {
    surface = env->NewGlobalRef(surface_);
    auto wid = (int64_t) (intptr_t) surface;
    mpv_set_option(reinterpret_cast<mpv_handle *>(handle), "wid", MPV_FORMAT_INT64, (void *) &wid);
}

jni_func(void, detachSurface, jlong handle) {
    int64_t wid = 0;
    mpv_set_option(reinterpret_cast<mpv_handle *>(handle), "wid", MPV_FORMAT_INT64, (void *) &wid);

    env->DeleteGlobalRef(surface);
    surface = nullptr;
}
