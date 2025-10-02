//
// Created by vkozhemiakin on 28.09.25.
//
#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <optional>
#include <memory>
#include <algorithm>

#include "filters/filters.h"

#define LOG_TAG "imgfilters"
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

struct BitmapLocker {
    JNIEnv *env;
    jobject bmp;
    void *pixels = nullptr;
    bool isSuccess = false;

    BitmapLocker(JNIEnv *e, jobject b) : env(e), bmp(b) {
        isSuccess = (AndroidBitmap_lockPixels(env, bmp, &pixels) == ANDROID_BITMAP_RESULT_SUCCESS);
    }

    ~BitmapLocker() {
        if (isSuccess) AndroidBitmap_unlockPixels(env, bmp);
    }
};

struct LockedImage {
    imgfilters::ImageView view;
    std::unique_ptr<BitmapLocker> lock;
};

static bool info_rgba8888(JNIEnv *env, jobject bitmap, AndroidBitmapInfo *out) {
    if (AndroidBitmap_getInfo(env, bitmap, out) != ANDROID_BITMAP_RESULT_SUCCESS) {
        return false;
    }
    if (out->format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        ALOGE("Bitmap not RGBA_8888, format=%d", out->format);
        return false;
    }
    return true;
}

static std::optional<LockedImage> make_locked_image(JNIEnv *env, jobject bitmap) {
    if (!bitmap) {
        return std::nullopt;
    }

    AndroidBitmapInfo info{};
    if (!info_rgba8888(env, bitmap, &info)) {
        return std::nullopt;
    }

    auto lock = std::make_unique<BitmapLocker>(env, bitmap);
    if (!lock->isSuccess || !lock->pixels) {
        ALOGE("AndroidBitmap_lockPixels failed");
        return std::nullopt;
    }

    imgfilters::ImageView view{
            static_cast<uint8_t *>(lock->pixels),
            info.width, info.height, info.stride
    };
    return LockedImage{view, std::move(lock)};
}

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_vkozhemi_mylibrary_NativeFilters_applyInvert(
        JNIEnv *env, jclass /*clazz*/, jobject bitmap) {
    auto li = make_locked_image(env, bitmap);
    if (!li) {
        return JNI_FALSE;
    }
    return imgfilters::apply_invert(li->view) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_vkozhemi_mylibrary_NativeFilters_applyBlur(
        JNIEnv *env, jclass /*clazz*/, jobject bitmap, jint radius) {
    auto li = make_locked_image(env, bitmap);
    if (!li) {
        return JNI_FALSE;
    }

    const int r = std::max(0, static_cast<int>(radius));
    return imgfilters::apply_box_blur(li->view, r) ? JNI_TRUE : JNI_FALSE;
}

} // extern "C"
