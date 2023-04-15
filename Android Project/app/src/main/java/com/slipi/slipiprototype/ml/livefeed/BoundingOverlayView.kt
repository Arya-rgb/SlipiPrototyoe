package com.slipi.slipiprototype.ml.livefeed
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.slipi.slipiprototype.ml.detectormachine.Detector
import com.slipi.slipiprototype.ml.detectormachine.TFLiteObjectDetectionAPIModel

class BoundingOverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val boxPaint = Paint()
    private val path = Path()
    private var results: List<Detector.Recognition> = listOf()

    init {
        boxPaint.color = Color.RED
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = 4f
    }

    fun setResults(results: List<Detector.Recognition>) {
        this.results = results
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        // Clip canvas to the bounds of the view
        path.reset()
        path.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        canvas.clipPath(path)

        // Draw bounding boxes
        for (result in results) {
            val location = result.location
            canvas.drawRect(location, boxPaint)
        }
    }
}