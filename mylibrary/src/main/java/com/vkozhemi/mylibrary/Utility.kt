package com.vkozhemi.mylibrary

import android.graphics.Bitmap

/**
 * Ensure bitmap is mutable ARGB_8888
 */
fun Bitmap.ensureMutableArgb8888(): Bitmap =
    if (config == Bitmap.Config.ARGB_8888 && isMutable) this
    else copy(Bitmap.Config.ARGB_8888, true)