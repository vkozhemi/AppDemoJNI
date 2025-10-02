package com.vkozhemi.mylibrary

import android.graphics.Bitmap
import androidx.annotation.Keep

object NativeFilters {
    init {
        System.loadLibrary("imgfilters")
    }

    /**
     * Invert picture colors.
     *
     * @return true if succeeded, otherwise - false
     */
    @Keep
    @JvmStatic
    external fun applyInvert(bitmap: Bitmap): Boolean

    /**
     * Blur picture with specified radius.
     *
     * @return true if succeeded, otherwise - false
     */
    @Keep
    @JvmStatic
    external fun applyBlur(bitmap: Bitmap, radius: Int): Boolean
}
