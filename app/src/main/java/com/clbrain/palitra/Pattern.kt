package com.clbrain.palitra

import android.graphics.RectF
import kotlin.math.max
import kotlin.math.min

class Pattern(
    val image: RectF,
    private val colors: MutableList<RectF>
) {
    val count = colors.size

    fun getPatternBounds(i: Int): RectF {
        return if (i == IMAGE_INDEX) {
            image
        } else {
            colors[i]
        }
    }

    val maxX: Float
        get() = max(image.right, colors.map { it.right }.max() ?: image.right)
    val minX: Float
        get() = min(image.left, colors.map { it.left }.min() ?: image.left)

    val maxY: Float
        get() = max(image.bottom, colors.map { it.bottom }.max() ?: image.bottom)
    val minY: Float
        get() = min(image.top, colors.map { it.top }.min() ?: image.top)

    companion object {
        const val MAX_COUNT_OF_COLORS = 5
        const val IMAGE_INDEX = -1

        val BOTTOM_TRIPTYCH = Pattern(
            RectF(0f, 0f, 1f, 1f),
            mutableListOf(
                RectF(0f, 1f, 1f / 3f, 4f / 3f),
                RectF(1f / 3f, 1f, 2f / 3f, 4f / 3f),
                RectF(2f / 3f, 1f, 1f, 4f / 3f)
            )
        )
        val LEFT_BOTTOM_CORNER_TRIPTYCH = Pattern(
            RectF(1f / 3, 0f, 4f / 3, 1f),
            mutableListOf(
                RectF(0f, 0f, 1f / 3f, 1f),
                RectF(0f, 1f, 1f / 3f, 4f / 3f),
                RectF(1f / 3f, 1f, 4f / 3, 4f / 3f)
            )
        )
        val LEFT_TRIPTYCH = Pattern(
            RectF(0f, 0f, 1f, 1f),
            mutableListOf(
                RectF(1f, 0f, 4f / 3, 1f / 3),
                RectF(1f, 1f / 3, 4f / 3, 2f / 3f),
                RectF(1f, 2f / 3, 4f / 3, 1f)
            )
        )
        val BOTTOM_FOUR_LINES = Pattern(
            RectF(0f, 0f, 1f, 1f),
            mutableListOf(
                RectF(0f, 6f / 6, 1f, 7f / 6),
                RectF(0f, 7f / 6, 1f, 8f / 6),
                RectF(0f, 8f / 6, 1f, 9f / 6),
                RectF(0f, 9f / 6, 1f, 10f / 6)
            )
        )
        val BOTTOM_FOUR_POLYPTYCH = Pattern(
            RectF(0f, 0f, 1f, 1f),
            mutableListOf(
                RectF(0f, 1f, 1f / 4f, 4f / 3f),
                RectF(1f / 4f, 1f, 1f / 2f, 4f / 3f),
                RectF(1f / 2f, 1f, 3f / 4f, 4f / 3f),
                RectF(3f / 4f, 1f, 1f, 4f / 3f)
            )
        )
        val BOTTOM_FIVE_POLYPTYCH = Pattern(
            RectF(0f, 0f, 1f, 1f),
            mutableListOf(
                RectF(0f, 1f, 1f / 5f, 4f / 3f),
                RectF(1f / 5f, 1f, 2f / 5f, 4f / 3f),
                RectF(2f / 5f, 1f, 3f / 5f, 4f / 3f),
                RectF(3f / 5f, 1f, 4f / 5f, 4f / 3f),
                RectF(4f / 5f, 1f, 1f, 4f / 3f)
            )
        )

        val PATTERNS = listOf(
            BOTTOM_TRIPTYCH,
            LEFT_BOTTOM_CORNER_TRIPTYCH,
            LEFT_TRIPTYCH,
            BOTTOM_FOUR_LINES,
            BOTTOM_FOUR_POLYPTYCH,
            BOTTOM_FIVE_POLYPTYCH
        )
    }
}