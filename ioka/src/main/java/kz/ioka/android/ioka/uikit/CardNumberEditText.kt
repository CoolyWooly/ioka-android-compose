package kz.ioka.android.ioka.uikit

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.text.InputFilter
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.setPadding
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.presentation.flows.common.CardBrandDvo
import kz.ioka.android.ioka.presentation.flows.common.CardEmitterDvo
import kz.ioka.android.ioka.util.Optional
import kz.ioka.android.ioka.util.getDrawableFromRes
import kz.ioka.android.ioka.util.textChanges
import kz.ioka.android.ioka.util.toPx
import kotlin.math.ceil
import kotlin.math.roundToInt

internal class CardNumberEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val validator = CardNumberValidator()

    private lateinit var etCardNumber: AppCompatEditText
    private lateinit var ivEmitter: AppCompatImageView
    private lateinit var ivBrand: AppCompatImageView
    private lateinit var btnScan: AppCompatImageButton

    var onScanClicked: () -> Unit = {}
    var onTextChanged: (String) -> Unit = {}
    var onTextChangedWithDebounce: (String) -> Unit = {}
    var flowTextChangedWithDebounce: Flow<CharSequence?> = flow { }

    var isValid = MutableLiveData(false)
    private var isValidationEnabled = false

    init {
        val root =
            LayoutInflater.from(context).inflate(R.layout.ioka_card_number_edit_text, this, true)

        bindViews(root)
        setupViews()
        setupListeners()
    }

    private fun bindViews(root: View) {
        etCardNumber = root.findViewById(R.id.etCardNumber)
        ivEmitter = root.findViewById(R.id.ivEmitter)
        ivBrand = root.findViewById(R.id.ivBrand)
        btnScan = root.findViewById(R.id.btnScan)
    }

    private fun setupViews() {
        orientation = HORIZONTAL
        background = AppCompatResources.getDrawable(context, R.drawable.ioka_bg_edittext)
        gravity = Gravity.CENTER_VERTICAL
        setPadding(8.toPx.toInt())
    }

    private fun setupListeners() {
        etCardNumber.doOnTextChanged { text, _, _, _ ->
            val newIsValid = validator.validate(text.toString().replace(" ", ""))
            val isValidationChanged = this.isValid.value != newIsValid
            this.isValid.value = newIsValid

            if (isValidationChanged && isValidationEnabled) {
                setValidationStroke(!(isValid.value ?: false))
            }
        }

        etCardNumber.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !isValidationEnabled && getCardNumber().isNotEmpty()) {
                isValidationEnabled = true

                setValidationStroke(!(isValid.value ?: false))
            }
        }

        flowTextChangedWithDebounce = etCardNumber.textChanges().debounce(100).onEach {
            onTextChangedWithDebounce(it.toString().replace(" ", ""))
        }

        btnScan.setOnClickListener {
            onScanClicked()
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

    fun getCardNumber(): String {
        return etCardNumber.text.toString().replace(" ", "")
    }

    fun setBrand(brandOptional: Optional<CardBrandDvo>) {
        brandOptional.getIfNull()?.iconRes?.let {
            ivBrand.isInvisible = false
            ivBrand.setImageDrawable(context.getDrawableFromRes(it))
        } ?: run {
            ivBrand.isInvisible = true
        }
    }

    fun setEmitter(emitterOptional: Optional<CardEmitterDvo>) {
        emitterOptional.getIfNull()?.iconRes?.let {
            ivEmitter.isInvisible = false
            ivEmitter.setImageDrawable(context.getDrawableFromRes(it))
        } ?: run {
            ivEmitter.isInvisible = true
        }
    }

    fun setCardNumber(cardNumber: String) {
        etCardNumber.setText(cardNumber, TextView.BufferType.EDITABLE)
    }

    fun setIconColor(@ColorRes iconColor: Int) {
        ImageViewCompat.setImageTintList(
            btnScan,
            ColorStateList.valueOf(ContextCompat.getColor(context, iconColor))
        )
    }

    fun setCardNumberLengthRange(lengthRange: IntRange) {
        val maxCount = lengthRange.last
        val sectionsCount = maxCount / 4.0
        val roundedCount = ceil(sectionsCount).roundToInt() - 1

        etCardNumber.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(maxCount + roundedCount))

        validator.setCardNumberLengthRange(lengthRange)
    }

    fun clear() {
        etCardNumber.setText("")
        isValidationEnabled = false
        isValid.value = false

        setValidationStroke(false)
    }

    override fun setEnabled(enabled: Boolean) {
        etCardNumber.isEnabled = enabled
        btnScan.isEnabled = enabled

        super.setEnabled(enabled)
    }

}