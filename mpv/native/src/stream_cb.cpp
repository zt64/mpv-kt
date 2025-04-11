#include "jni_utils.h"
#include <mpv/stream_cb.h>

jni_func(void, streamCbAddRo, const jlong handle, jstring protocol_, jobject open_fn) {
    const char *protocol = env->GetStringUTFChars(protocol_, nullptr);
    const mpv_stream_cb_open_ro_fn open = [](auto ctx, auto uri, auto *info) -> int {
        // info->cookie = fp;
        // info->size_fn = size_fn;
        // info->read_fn = read_fn;
        // info->seek_fn = seek_fn;
        // info->close_fn = close_fn;
        return 0;
    };

    mpv_stream_cb_add_ro(reinterpret_cast<mpv_handle *>(handle), protocol, nullptr, open);

    env->ReleaseStringUTFChars(protocol_, protocol);
}