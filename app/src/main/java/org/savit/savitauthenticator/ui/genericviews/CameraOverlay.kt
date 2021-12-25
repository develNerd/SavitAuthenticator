package org.savit.savitauthenticator.ui.genericviews

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import org.savit.savitauthenticator.R


open class CameraOverlay : LinearLayout {
    private var bitmap: Bitmap? = null
    private var rectangle:Rect? = null


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (bitmap == null) {
            createWindowFrame()
        }
        canvas.drawBitmap(bitmap!!,0f,0f,null)
    }

    private fun createWindowFrame() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val osCanvas = Canvas(bitmap!!)
        val outerRectangle = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val canvasW = width
        val canvasH = height
        val centerOfCanvas = Point(canvasW / 2, canvasH / 2)
        val rectW = canvasW / 2.2
        val rectH = canvasH / 4.5
        val left = centerOfCanvas.x - rectW / 2
        val top = centerOfCanvas.y - rectH / 2
        val right = centerOfCanvas.x + rectW / 2
        val bottom = centerOfCanvas.y + rectH / 2
        val rect = RectF(left.toFloat(),top.toFloat(),right.toFloat(),bottom.toFloat())
        val rectSrokeW =  canvasW / 2.2
        val rectStrokeH = canvasH / 4.5
        val leftS = centerOfCanvas.x - rectSrokeW / 2
        val topS = centerOfCanvas.y - rectStrokeH / 2
        val rightS = centerOfCanvas.x + rectSrokeW / 2
        val bottomS = centerOfCanvas.y + rectStrokeH / 2
        val rectStroke = RectF(leftS.toFloat(),topS.toFloat(),rightS.toFloat(),bottomS.toFloat())

        // create a rectangle that we'll draw later
        // create a rectangle that we'll draw later
        paint.color = resources.getColor(R.color.black)
        paint.alpha = 99
        osCanvas.drawRect(outerRectangle, paint)
        paint.color = Color.TRANSPARENT
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        osCanvas.drawRoundRect(rect,10f,10f, paint)
        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        paint.strokeWidth = 4f
        paint.strokeCap = Paint.Cap.ROUND
        osCanvas.drawRoundRect(rectStroke,10f,10f, paint)


    }

    override fun isInEditMode(): Boolean {
        return true
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        bitmap = null
    }
}
