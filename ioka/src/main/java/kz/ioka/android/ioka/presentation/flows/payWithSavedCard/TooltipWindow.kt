package kz.ioka.android.ioka.presentation.flows.payWithSavedCard

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import kz.ioka.android.ioka.R

internal class TooltipWindow(ctx: Context) {

    private val tipWindow: PopupWindow?
    private val contentView: View
    private val inflater: LayoutInflater

    init {
        tipWindow = PopupWindow(ctx)
        inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        contentView = inflater.inflate(R.layout.view_tooltip, null)
    }

    fun showToolTip(anchor: View) {
        tipWindow!!.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
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

        val positionX: Int = anchorRect.centerX() - contentViewWidth / 2
        val positionY: Int = anchorRect.bottom - anchorRect.height() / 2
        tipWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, positionX, positionY)

        Handler(Looper.getMainLooper()).postDelayed({
            if (tipWindow.isShowing) {
                tipWindow.dismiss()
            }
        }, 3000)
    }

    val isTooltipShown: Boolean
        get() = tipWindow != null && tipWindow.isShowing
}