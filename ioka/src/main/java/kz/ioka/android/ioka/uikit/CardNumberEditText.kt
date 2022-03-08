package kz.ioka.android.ioka.uikit

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.util.*

class CardNumberEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private lateinit var etCardNumber: AppCompatEditText
    private lateinit var ivEmitter: AppCompatImageView
    private lateinit var ivBrand: AppCompatImageView
    private lateinit var btnScan: AppCompatImageButton

    var onTextChanged: (String) -> Unit = {}
    var onTextChangedWithDebounce: (String) -> Unit = {}

    var flowTextChangedWithDebounce: Flow<CharSequence?> = flow { }

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.view_card_number_input, this, true)

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
        background = AppCompatResources.getDrawable(context, R.drawable.bg_edittext)
        gravity = Gravity.CENTER_VERTICAL
        setPadding(16.toPx.toInt())
    }

    private fun setupListeners() {
        flowTextChangedWithDebounce = etCardNumber.textChanges().debounce(200).onEach {
            onTextChangedWithDebounce(it.toString().replace(" ", ""))
        }

        etCardNumber.doOnTextChanged { text, start, before, count ->
            onTextChanged(text.toString().replace(" ", ""))
        }

        etCardNumber.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            val strokeWidth = if (hasFocus) 1.toPx.toInt() else 0

            val back = background as GradientDrawable

            back.mutate()
            back.setStroke(
                strokeWidth,
                ContextCompat.getColor(context, R.color.ioka_color_primary)
            )

            background = back
        }

        btnScan.setOnClickListener {
            // TODO SCANNER NAVIGATION
        }
    }

    fun setRadius(cornerRadius: Float) {
        (background as GradientDrawable).cornerRadius = cornerRadius.toPx

    }

    fun setTypeface(typeface: Typeface) {
        etCardNumber.typeface = typeface
    }

    fun setBrand(brandOptional: Optional<Int>) {
        if (brandOptional.isPresent()) {
            ivBrand.setImageDrawable(context.getDrawableFromRes(brandOptional.get()))
        }

        ivBrand.isInvisible = brandOptional.isNotPresent()
    }

    fun setEmitter(emitterOptional: Optional<Int>) {
        if (emitterOptional.isPresent()) {
            ivEmitter.setImageDrawable(context.getDrawableFromRes(emitterOptional.get()))
        }

        ivEmitter.isInvisible = emitterOptional.isNotPresent()
    }

    fun setError(isWrong: Boolean) {
        etCardNumber.setWrongFormatError(isWrong)
    }

    fun getCardNumber(): String {
        return etCardNumber.text.toString().replace(" ", "")
    }

}