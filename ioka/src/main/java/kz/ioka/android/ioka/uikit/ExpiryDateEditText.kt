package kz.ioka.android.ioka.uikit

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.util.toPx
import kotlin.math.roundToInt

internal class ExpiryDateEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle
) : FormattedNumberEditText(context, attrs, defStyleAttr) {

    private val validator = ExpiryDateValidator()

    var isValid = MutableLiveData(false)
    private var isValidationEnabled = false

    init {
        background = AppCompatResources.getDrawable(context, R.drawable.ioka_bg_edittext)

        doOnTextChanged { text, _, _, _ ->
            val newIsValid = validator.validate(text.toString().replace("/", ""))
            val isValidationChanged = this.isValid.value != newIsValid
            this.isValid.value = newIsValid

            if (isValidationChanged) {
                setValidationStroke(!(isValid.value ?: false))
            }
        }

        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !isValidationEnabled && getExpiryDate().isNotEmpty()) {
                isValidationEnabled = true

                setValidationStroke(!(isValid.value ?: false))
            }
        }
    }

    private fun setValidationStroke(isError: Boolean) {
        val (strokeWidth, strokeColor) = if (!isError) {
            0.toPx.roundToInt() to R.color.ioka_color_nonadaptable_transparent
        } else {
            1.toPx.roundToInt() to R.color.ioka_color_error
        }

        val back = background as GradientDrawable

        back.mutate()
        back.setStroke(strokeWidth, ContextCompat.getColor(context, strokeColor))

        background = back
    }

    fun getExpiryDate(): String {
        return text.toString()
    }

    fun clear() {
        setText("")
        isValidationEnabled = false
        isValid.value = false

        setValidationStroke(false)
    }

}