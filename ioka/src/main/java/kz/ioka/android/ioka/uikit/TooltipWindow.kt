package kz.ioka.android.ioka.uikit

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.*
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.util.toPx


internal class TooltipWindow(ctx: Context) {

    companion object {
        const val TOOLTIP_ARROW_WIDTH = 16
        const val TOOLTIP_ARROW_MARGIN_END = 16

        const val SHOW_DURATION = 3_000L
        const val ANIMATION_DURATION = 200L

        const val START_ALPHA = 1f
        const val FINAL_ALPHA = 0f
    }

    private var tipWindow: PopupWindow = PopupWindow(ctx)
    private val contentView: View
    private val inflater: LayoutInflater

    private val dismissRunnable by lazy {
        Runnable {
            if (isTooltipShown) {
                dismissWithAnimation()
            }
        }
    }

    private val myHandler = Handler(Looper.getMainLooper())

    init {
        tipWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                ctx,
                R.color.ioka_color_nonadaptable_transparent
            )
        )
        inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        contentView = inflater.inflate(R.layout.ioka_tooltip, null)

        tipWindow.setOnDismissListener {
            myHandler.removeCallbacks(dismissRunnable)
        }
    }

    fun showToolTip(anchor: View) {
        if (isTooltipShown) return

        tipWindow.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        tipWindow.width = ConstraintLayout.LayoutParams.WRAP_CONTENT

        tipWindow.isOutsideTouchable = true
        tipWindow.isTouchable = true
        tipWindow.isFocusable = true

        tipWindow.contentView = contentView

        val anchorRect = getRect(anchor)

        contentView.measure(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        val contentViewWidth: Int = contentView.measuredWidth

        val arrowX = anchorRect.centerX() - TOOLTIP_ARROW_WIDTH.toPx.toInt() / 2

        val positionX: Int =
            arrowX - contentViewWidth + TOOLTIP_ARROW_WIDTH.toPx.toInt() + TOOLTIP_ARROW_MARGIN_END.toPx.toInt()
        val positionY: Int =
            anchorRect.top - contentView.measuredHeight - TOOLTIP_ARROW_MARGIN_END.toPx.toInt() / 2

        tipWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, positionX, positionY)

        myHandler.postDelayed(dismissRunnable, SHOW_DURATION)
    }

    private fun getRect(view: View): Rect {
        val screenPos = IntArray(2)

        view.getLocationInWindow(screenPos)

        return Rect(
            screenPos[0], screenPos[1], screenPos[0] + view.width, screenPos[1] + view.height
        )
    }

    private fun dismissWithAnimation() {
        val fadeOut: Animation = AlphaAnimation(START_ALPHA, FINAL_ALPHA)

        fadeOut.duration = ANIMATION_DURATION

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                tipWindow.dismiss()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        tipWindow.contentView.startAnimation(fadeOut)
    }

    val isTooltipShown: Boolean
        get() = tipWindow.isShowing
}