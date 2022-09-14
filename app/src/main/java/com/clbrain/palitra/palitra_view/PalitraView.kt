package com.clbrain.palitra.palitra_view

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.clbrain.palitra.ColorUtils.colorToHexString
import com.clbrain.palitra.ColorUtils.isBrightColor
import com.clbrain.palitra.MainActivity
import com.clbrain.palitra.Pattern
import com.clbrain.palitra.R
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class PalitraView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {
    private var pattern = Pattern.BOTTOM_TRIPTYCH
    private var currentImage: Bitmap? = null
    private var savedPicture: Bitmap? = null
    private var viewWidth = 0f
    private var viewHeight = 0f
    private val mainPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }
    private val formsPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = resources.getColor(R.color.colorPrimaryLight)
        strokeWidth = 10f
    }
    private val colorPickerLight = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = colorPickerStrokeWidth
        color = Color.WHITE
        setShadowLayer(12f, 0f, 0f, Color.WHITE)
        setLayerType(LAYER_TYPE_SOFTWARE, this)
    }
    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        textSize = 30f
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }
    private var writeHEX = false
    private var startX = 0f
    private var startY = 0f
    private var imgWidth = 0
    private var imgHeight = 0

    //color picker params
    private val colorPickerX = FloatArray(Pattern.MAX_COUNT_OF_COLORS)
    private val colorPickerY = FloatArray(Pattern.MAX_COUNT_OF_COLORS)
    private val colorPickerR = FloatArray(Pattern.MAX_COUNT_OF_COLORS) { 0f }
    private var colorPickerStrokeWidth = 10f
    private val colorPickerPaint = List(
        Pattern.MAX_COUNT_OF_COLORS,
        fun(_) = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.WHITE
            strokeWidth = colorPickerStrokeWidth
        }
    )
    private var chosenColorPickerIndex = 0
    private val colorPaints = List(
        Pattern.MAX_COUNT_OF_COLORS,
        fun(_) = Paint().apply {
            style = Paint.Style.FILL
            color = Color.WHITE
        }
    )
    private val defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.add_image_img)

    private fun fromImageToViewX(x: Float): Float {
        return x + startX
    }

    private fun fromImageToViewY(y: Float): Float {
        return y + startY
    }

    private fun fromViewToImageX(x: Float): Float {
        return x - startX
    }

    private fun fromViewToImageY(y: Float): Float {
        return y - startY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (currentImage == null) {
            if (event.action == MotionEvent.ACTION_UP) {
                try {
                    (context as MainActivity).addImage()
                } catch (ignored: Exception) {
                }
            }
            return true
        }
        var x = event.x
        var y = event.y
        val imgX = fromViewToImageX(x).toInt()
            .coerceAtLeast(0)
            .coerceAtMost(currentImage!!.width - 1)
        val imgY = fromViewToImageY(y).toInt()
            .coerceAtLeast(0)
            .coerceAtMost(currentImage!!.height - 1)
        x = fromImageToViewX(imgX.toFloat())
        y = fromImageToViewY(imgY.toFloat())
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                for (i in 0 until pattern.count) {
                    if (distance(x, y, colorPickerX[i], colorPickerY[i]) <
                        distance(x, y, colorPickerX[chosenColorPickerIndex], colorPickerY[chosenColorPickerIndex])) {
                        chosenColorPickerIndex = i
                    }
                }
                val colorPickerAnimator = ValueAnimator()
                colorPickerAnimator.setFloatValues(30f, 50f)
                colorPickerAnimator.duration = 200
                chosenColorPickerIndex.let {
                    colorPickerAnimator.addUpdateListener { animation: ValueAnimator ->
                        colorPickerR[it] = animation.animatedValue as Float
                        invalidate()
                    }
                    val xAnimator = ValueAnimator()
                    xAnimator.setFloatValues(colorPickerX[it], x)
                    xAnimator.duration = 100
                    xAnimator.addUpdateListener { animation: ValueAnimator -> colorPickerX[it] = animation.animatedValue as Float }
                    val yAnimator = ValueAnimator()
                    yAnimator.setFloatValues(colorPickerY[it], y)
                    yAnimator.duration = 100
                    yAnimator.addUpdateListener { animation: ValueAnimator -> colorPickerY[it] = animation.animatedValue as Float }
                    val set = AnimatorSet()
                    set.playTogether(xAnimator, yAnimator, colorPickerAnimator)
                    set.start()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                colorPickerX[chosenColorPickerIndex] = x
                colorPickerY[chosenColorPickerIndex] = y
            }
            MotionEvent.ACTION_UP -> {
                val colorPickerAnimator = ValueAnimator()
                colorPickerAnimator.setFloatValues(50f, 30f)
                colorPickerAnimator.duration = 200
                colorPickerAnimator.addUpdateListener { animation: ValueAnimator ->
                    colorPickerR[chosenColorPickerIndex] = animation.animatedValue as Float
                    invalidate()
                }
                colorPickerAnimator.start()
            }
        }
        colorPickerPaint[chosenColorPickerIndex].color = currentImage!!.getPixel(imgX, imgY)
        colorPaints[chosenColorPickerIndex].color = currentImage!!.getPixel(imgX, imgY)
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        val currentImage = currentImage
        if (currentImage != null) {
            drawCurrentImage(canvas, currentImage)
            for (i in 0 until pattern.count) {
                drawColorRect(canvas, pattern.getPatternBounds(i), width, height, colorPaints[i])
                drawColorPicker(canvas, colorPickerX[i], colorPickerY[i], colorPickerR[i], colorPickerPaint[i])
            }
        } else {
            drawStub(canvas)
        }
    }

    private fun drawStub(canvas: Canvas) {
        canvas.drawBitmap(defaultBitmap, (width - defaultBitmap.width) / 2f, (height - defaultBitmap.height) / 2f, null)
    }

    fun save() {
        if (currentImage != null) {
            val bitmap = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas()
            canvas.setBitmap(bitmap)
            canvas.drawBitmap(currentImage!!, startX - (width - viewWidth) / 2f, startY - (height - viewHeight) / 2f, mainPaint)
            for (i in 0 until pattern.count) {
                drawColorRect(canvas, pattern.getPatternBounds(i), viewWidth.toInt(), viewHeight.toInt(), colorPaints[i])
            }
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, UUID.randomUUID().toString() + ".jpg", "palitra")
        }
    }

    fun setWriteHEX(writeHEX: Boolean) {
        this.writeHEX = writeHEX
        invalidate()
    }

    fun setPattern(pattern: Pattern) {
        if (currentImage != null) {
            for (i in 0 until pattern.count) {
                colorPickerX[i] = fromViewToImageX(colorPickerX[i]) / imgWidth
                colorPickerY[i] = fromViewToImageY(colorPickerY[i]) / imgHeight
            }
            this.pattern = pattern
            calculateParams()
            for (i in 0 until pattern.count) {
                colorPickerX[i] = fromImageToViewX(colorPickerX[i] * imgWidth)
                colorPickerY[i] = fromImageToViewY(colorPickerY[i] * imgHeight)
            }
            invalidate()
        } else {
            this.pattern = pattern
        }
    }

    fun setCurrentImage(currentImage: Bitmap?) {
        savedPicture = currentImage
        if (currentImage == null) {
            this.currentImage = null
            invalidate()
            return
        }
        calculateParams()
        for ((i, colorPaint) in colorPaints.withIndex()) {
            val x = (this.currentImage!!.width * Math.random()).toInt()
            val y = (this.currentImage!!.height * Math.random()).toInt()
            colorPaint.color = this.currentImage!!.getPixel(x, y)
            colorPickerPaint[i].color = this.currentImage!!.getPixel(x, y)
            colorPickerX[i] = fromImageToViewX(x.toFloat())
            colorPickerY[i] = fromImageToViewY(y.toFloat())
        }
        for (i in colorPickerR.indices) {
            colorPickerR[i] = 30f
        }
        invalidate()
    }

    private fun calculateParams() {
        if (savedPicture == null) {
            return
        }
        val height = savedPicture!!.height * (pattern.maxY - pattern.minY)
        val width = savedPicture!!.width * (pattern.maxX - pattern.minX)
        val tg = height / width
        val viewTg = getHeight().toFloat() / getWidth()
        val imgTg = savedPicture!!.height.toFloat() / savedPicture!!.width
        if (tg > viewTg) {
            imgHeight = (getHeight() / (pattern.maxY - pattern.minY) * pattern.image.height()).toInt()
            imgWidth = (imgHeight / imgTg).toInt()
        } else {
            imgWidth = (getWidth() / (pattern.maxX - pattern.minX) * pattern.image.width()).toInt()
            imgHeight = (imgWidth * imgTg).toInt()
        }
        viewWidth = imgWidth * (pattern.maxX - pattern.minX)
        viewHeight = imgHeight * (pattern.maxY - pattern.minY)
        startX = (getWidth() - viewWidth) / 2 + viewWidth * (pattern.image.left - pattern.minX) / (pattern.maxX - pattern.minX)
        startY = (getHeight() - viewHeight) / 2 + viewHeight * (pattern.image.top - pattern.minY) / (pattern.maxY - pattern.minY)
        currentImage = Bitmap.createScaledBitmap(savedPicture!!, imgWidth, imgHeight, true)
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt(sqr(x1 - x2) + sqr(y1 - y2))
    }

    private fun drawColorRect(canvas: Canvas, colorRect: RectF, realWidth: Int, realHeight: Int, colorPaint: Paint) {
        val x = (realWidth - viewWidth) / 2 + viewWidth * (colorRect.left - pattern.minX) / (pattern.maxX - pattern.minX)
        val y = (realHeight - viewHeight) / 2 + viewHeight * (colorRect.top - pattern.minY) / (pattern.maxY - pattern.minY)
        val rectWidth = viewWidth * colorRect.width() / (pattern.maxX - pattern.minX)
        val rectHeight = viewHeight * colorRect.height() / (pattern.maxY - pattern.minY)

        canvas.drawRect(x, y, x + rectWidth, y + rectHeight, colorPaint)
        if (writeHEX) {
            val colorHex = colorToHexString(colorPaint.color)
            if (isBrightColor(colorPaint.color)) {
                textPaint.color = Color.BLACK
            } else {
                textPaint.color = Color.WHITE
            }
            canvas.drawText(
                colorHex,
                x + formsPaint.strokeWidth,
                y + textPaint.textSize + formsPaint.strokeWidth,
                textPaint
            )
        }
    }

    private fun drawCurrentImage(canvas: Canvas, image: Bitmap) {
        canvas.drawBitmap(image, startX, startY, mainPaint)
    }

    private fun drawColorPicker(canvas: Canvas, x: Float, y: Float, radius: Float, colorPickerPaint: Paint) {
        canvas.drawCircle(x, y, radius, colorPickerLight)
        canvas.drawCircle(x, y, radius, colorPickerPaint)
    }

    private fun sqr(x: Float): Float {
        return x * x
    }

    fun canBeSaved() = savedPicture != null
}