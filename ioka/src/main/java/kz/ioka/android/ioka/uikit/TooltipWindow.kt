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

    private var tipWindow: PopupWindow = PopupWindow(ctx)
    private val contentView: View
    private val inflater: LayoutInflater

    private val dismissRunnable by lazy {
        Runnable {
            if (tipWindow.isShowing) {
                val fadeOut: Animation = AlphaAnimation(1f, 0f)

                fadeOut.duration = 200

                fadeOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        tipWindow.dismiss()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })

                tipWindow.contentView.startAnimation(fadeOut)
            }
        }
    }

    private val myHandler = Handler(Looper.getMainLooper())

    init {
        tipWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                ctx,
                R.color.ioka_color_static_transparent
            )
        )
        inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        contentView = inflater.inflate(R.layout.view_tooltip, null)

        tipWindow.setOnDismissListener {
            myHandler.removeCallbacks(dismissRunnable)
        }
    }

    fun showToolTip(anchor: View) {
        tipWindow.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        tipWindow.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        tipWindow.isOutsideTouchable = true

        tipWindow.isTouchable = true
        tipWindow.isFocusable = true
        tipWindow.contentView = contentView
        val screenPos = IntArray(2)

        anchor.getLocationOnScreen(screenPos)

        val anchorRect = Rect(
            screenPos[0], screenPos[1], screenPos[0]
                + anchor.width, screenPos[1] + anchor.height
        )

        contentView.measure(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        val contentViewWidth: Int = contentView.measuredWidth

        val arrowX = anchorRect.centerX() + anchorRect.width() / 2

        val positionX: Int = arrowX - contentViewWidth - 4.toPx.toInt()
        val positionY: Int = anchorRect.top - contentView.measuredHeight + 8.toPx.toInt()

        tipWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, positionX, positionY)

        myHandler.postDelayed(dismissRunnable, 3000)
    }

    val isTooltipShown: Boolean
        get() = tipWindow.isShowing
}