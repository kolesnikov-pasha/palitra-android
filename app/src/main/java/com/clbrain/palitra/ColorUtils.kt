package com.clbrain.palitra

import android.graphics.Color
import androidx.annotation.ColorInt

object ColorUtils {
    @JvmStatic
    fun isBrightColor(@ColorInt color: Int): Boolean {
        Color.colorToHSV(color, HSV_TEMPORARY)
        return HSV_TEMPORARY[2] > BRIGHT_VALUE
    }

    @JvmStatic
    fun colorToHexString(@ColorInt color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }

    private const val BRIGHT_VALUE = 0.8f
    private val HSV_TEMPORARY = floatArrayOf(0f, 0f, 0f)
}