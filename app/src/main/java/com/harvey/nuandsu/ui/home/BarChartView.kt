package com.harvey.nuandsu.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.harvey.nuandsu.R

class BarChartView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val barPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        shader = LinearGradient(
            0f, 0f, 0f, 500f,
            intArrayOf(Color.rgb(115, 175, 111), Color.rgb(0, 126, 110)),
            null,
            Shader.TileMode.CLAMP
        )
    }


    private val baselinePaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 36f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    var values: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (values.isEmpty()) return

        val maxVal = values.maxOrNull() ?: 1f
        val barWidth = width / (values.size * 2f)

        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), baselinePaint)

        values.forEachIndexed { index, v ->
            val barHeight = (v / maxVal) * (height * 0.8f)
            val left = index * barWidth * 2f
            val top = height - barHeight
            val right = left + barWidth
            val bottom = height.toFloat()


            canvas.drawRect(left, top, right, bottom, barPaint)

        }
    }
}
