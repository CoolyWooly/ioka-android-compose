package kz.ioka.android.ioka.uikit

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.util.toPx
import kotlin.math.roundToInt

internal class CvvEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val validator = CvvValidator()

    private lateinit var etCvv: AppCompatEditText
    lateinit var ivCvvFaq: AppCompatImageView

    var onTextChanged: (String) -> Unit = {}
    var onFaqClicked: () -> Unit = {}

    var isValid = MutableLiveData(false)
    private var isValidationEnabled = false

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.ioka_cvv_edit_text, this, true)

        bindViews(root)
        setupViews()
        setupListeners()
    }

    private fun bindViews(root: View) {
        etCvv = root.findViewById(R.id.etCvv)
        ivCvvFaq = root.findViewById(R.id.ivCvvFaq)
    }

    private fun setupViews() {
        orientation = HORIZONTAL
        background = AppCompatResources.getDrawable(context, R.drawable.ioka_bg_edittext)
        gravity = Gravity.CENTER_VERTICAL
    }

    private fun setupListeners() {
        etCvv.doOnTextChanged { text, _, _, _ ->
            val newIsValid = validator.validate(text.toString())
            val isValidationChanged = this.isValid.value != newIsValid
            this.isValid.value = newIsValid

            if (isValidationChanged) {
                setValidationStroke(!(isValid.value ?: false))
            }
        }

        etCvv.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !isValidationEnabled && getCvv().isNotEmpty()) {
                isValidationEnabled = true

                setValidationStroke(!(isValid.value ?: false))
            }
        }

        ivCvvFaq.setOnClickListener {
            onFaqClicked()
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

    fun setIconColor(@ColorRes iconColor: Int) {
        ImageViewCompat.setImageTintList(
            ivCvvFaq,
            ColorStateList.valueOf(ContextCompat.getColor(context, iconColor))
        )
    }

    fun getCvv(): String {
        return etCvv.text.toString()
    }

    fun clear() {
        etCvv.setText("")
        isValidationEnabled = false
        isValid.value = false

        setValidationStroke(false)
    }

    override fun setEnabled(enabled: Boolean) {
        etCvv.isEnabled = enabled
        ivCvvFaq.isEnabled = enabled

        super.setEnabled(enabled)
    }

}