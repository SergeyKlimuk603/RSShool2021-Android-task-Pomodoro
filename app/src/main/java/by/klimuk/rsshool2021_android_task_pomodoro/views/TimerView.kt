package by.klimuk.rsshool2021_android_task_pomodoro.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import by.klimuk.rsshool2021_android_task_pomodoro.R

class TimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val FILL = 0
        private const val STROKE_WIDTH = 5F
        private const val DEF_ANGLE = 360F
    }

    private var angle = DEF_ANGLE
    private var color = 0
    private var style = FILL
    private val paint = Paint()

    init {
        if (attrs != null) {
            val styleAttrs = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TimerView,
                defStyleAttr,
                0
            )

            // Получаем цвет colorPrimary текущей темы в качестве цвета по умолчанию
            val typeValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorPrimary, typeValue, true)
            val defColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.resources.getColor(typeValue.resourceId, context.theme)
            } else {
                context.resources.getColor(typeValue.resourceId)
            }

            color = styleAttrs.getColor(R.styleable.TimerView_customColor, defColor)
            style = styleAttrs.getInt(R.styleable.TimerView_customStyle, FILL)
            angle = styleAttrs.getFloat(R.styleable.TimerView_angle, DEF_ANGLE)
            styleAttrs.recycle()
        }
        paint.color = color
        paint.style = if (style == FILL) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = STROKE_WIDTH
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (angle == 0F) return
        canvas?.drawArc(
            0F,
            0F,
            width.toFloat(),
            height.toFloat(),
            -90F,
            angle,
            true,
            paint
        )
    }

    fun setAngle(angle: Float) {
        this.angle = angle
        invalidate()
    }
}