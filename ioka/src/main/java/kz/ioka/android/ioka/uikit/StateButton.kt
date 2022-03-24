package kz.ioka.android.ioka.uikit

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.util.toPx

internal class StateButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private lateinit var tvTitle: AppCompatTextView
    private lateinit var ivState: AppCompatImageView
    private lateinit var vProgress: ProgressBar

    @ColorRes
    private var backgroundColorRes = R.color.ioka_color_primary
    private var callback: Callback? = null
    private lateinit var state: ButtonState

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.view_progress_button, this, true)

        bindViews(root)
        loadAndSetText(attrs, defStyleAttr)
    }

    private fun bindViews(root: View) {
        tvTitle = root.findViewById(R.id.tvTitle)
        ivState = root.findViewById(R.id.ivState)
        vProgress = root.findViewById(R.id.vProgress)
    }

    private fun loadAndSetText(attrs: AttributeSet?, defStyleAttr: Int) {
        val arr = context.obtainStyledAttributes(
            attrs,
            R.styleable.StateButton,
            defStyleAttr,
            0
        )

        val buttonText = arr.getResourceId(R.styleable.StateButton_sbText, 0)
        arr.recycle()

        if (buttonText != 0)
            tvTitle.text = context.getString(buttonText)
        cardElevation = 0f
        setCardBackgroundColor(ContextCompat.getColor(context, backgroundColorRes))
        radius = 12.toPx
    }

    fun setText(text: String) {
        tvTitle.text = text
    }

    fun setConfiguration(radius: Int, @ColorRes backgroundColor: Int, @StringRes textRes: Int) {
        this.backgroundColorRes = backgroundColor
        setCardBackgroundColor(ContextCompat.getColor(context, backgroundColor))
        this.radius = radius.toPx
        tvTitle.text = context.getText(textRes)
    }

    fun setTypeface(typeface: Typeface) {
        tvTitle.typeface = typeface
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setState(state: ButtonState) {
        this.state = state

        val backgroundColor =
            if (state == ButtonState.Disabled) R.color.ioka_color_text_secondary
            else backgroundColorRes
        this.setCardBackgroundColor(ContextCompat.getColor(context, backgroundColor))

        when (state) {
            ButtonState.Default -> {
                isClickable = true
                isFocusable = true

                tvTitle.isInvisible = false
                vProgress.isInvisible = true
                ivState.isInvisible = true
            }
            ButtonState.Disabled -> {
                isClickable = false
                isFocusable = false

                tvTitle.isInvisible = false
                vProgress.isInvisible = true
                ivState.isInvisible = true
            }
            ButtonState.Loading -> {
                isClickable = false
                isFocusable = false

                tvTitle.isInvisible = true
                vProgress.isInvisible = false
                ivState.isInvisible = true
            }
            ButtonState.Success -> {
                isClickable = false
                isFocusable = false

                ivState.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_check
                    )
                )

                tvTitle.isInvisible = true
                vProgress.isInvisible = true
                ivState.isInvisible = false

                val colorFrom = backgroundColorRes
                val colorTo = ContextCompat.getColor(context, R.color.ioka_color_static_green)
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                colorAnimation.duration = 250

                colorAnimation.addUpdateListener { animator ->
                    setCardBackgroundColor(animator.animatedValue as Int)

                    if ((animator.animatedValue as Int) == colorTo) {
                        callback?.onSuccess()?.invoke()
                    }
                }

                colorAnimation.start()
            }
        }
    }

}

sealed class ButtonState {

    object Default : ButtonState()
    object Disabled : ButtonState()
    object Loading : ButtonState()
    object Success : ButtonState()

}

interface Callback {

    fun onSuccess(): () -> Unit

}