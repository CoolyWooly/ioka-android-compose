package kz.ioka.android.ioka.uikit

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import kz.ioka.android.ioka.R

internal class IokaStateButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private lateinit var tvTitle: AppCompatTextView
    private lateinit var ivState: AppCompatImageView
    private lateinit var vProgress: ProgressBar

    private var resultCallback: ResultCallback? = null

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.ioka_state_button, this, true)

        bindViews(root)
        loadAndSetText(attrs, defStyleAttr)

        cardElevation = 0.toFloat()
        background = ContextCompat.getDrawable(context, R.drawable.ioka_bg_ioka_state_button)
    }

    private fun bindViews(root: View) {
        tvTitle = root.findViewById(R.id.tvTitle)
        ivState = root.findViewById(R.id.ivState)
        vProgress = root.findViewById(R.id.vProgress)
    }

    private fun loadAndSetText(attrs: AttributeSet?, defStyleAttr: Int) {
        val arr = context.obtainStyledAttributes(
            attrs,
            R.styleable.IokaStateButton,
            defStyleAttr,
            0
        )

        val buttonText = arr.getResourceId(R.styleable.IokaStateButton_sbText, 0)
        arr.recycle()

        if (buttonText != 0)
            tvTitle.text = context.getString(buttonText)
    }

    fun setText(text: String) {
        tvTitle.text = text
    }

    fun setCallback(callback: ResultCallback) {
        this.resultCallback = callback
    }

    fun setState(state: ButtonState) {
        tvTitle.isInvisible = state !is ButtonState.Default
        vProgress.isInvisible = state !is ButtonState.Loading
        ivState.isInvisible = state !is ButtonState.Success

        if (state is ButtonState.Success) {
            ivState.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ioka_ic_check
                )
            )

            tvTitle.isInvisible = true
            vProgress.isInvisible = true
            ivState.isInvisible = false

            val colorTo = ContextCompat.getColor(context, R.color.ioka_color_success)

            background.mutate()
            background.setTint(colorTo)

            post { resultCallback?.onSuccess()?.invoke() }
        }
    }
}

internal sealed class ButtonState {

    object Default : ButtonState()
    object Disabled : ButtonState()
    object Loading : ButtonState()
    object Success : ButtonState()

}

internal interface ResultCallback {

    fun onSuccess(): () -> Unit

}