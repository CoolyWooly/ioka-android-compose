package kz.ioka.android.ioka.uikit

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
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
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.setPadding
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.doOnTextChanged
import io.card.payment.CardIOActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.presentation.flows.common.CardBrandDvo
import kz.ioka.android.ioka.presentation.flows.common.CardEmitterDvo
import kz.ioka.android.ioka.util.*
import kz.ioka.android.ioka.util.Optional
import kz.ioka.android.ioka.util.getDrawableFromRes
import kz.ioka.android.ioka.util.textChanges
import kz.ioka.android.ioka.util.toPx
import kz.ioka.android.ioka.viewBase.BaseActivity
import kz.ioka.android.ioka.viewBase.BaseActivity.Companion.SCAN_REQUEST_CODE

internal class CardNumberEditText @JvmOverloads constructor(
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
        flowTextChangedWithDebounce = etCardNumber.textChanges().debounce(100).onEach {
            onTextChangedWithDebounce(it.toString().replace(" ", ""))
        }

        etCardNumber.doOnTextChanged { text, _, _, _ ->
            onTextChanged(text.toString().replace(" ", ""))
        }

        etCardNumber.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            val strokeWidth = if (hasFocus) 1.toPx.toInt() else 0

            val back = background as GradientDrawable

            back.mutate()
            back.setStroke(
                strokeWidth,
                context.getPrimaryColor()
            )

            background = back
        }

        btnScan.setOnClickListener {
            val scanIntent = Intent(context, CardIOActivity::class.java)

            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
            scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false)
            scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true)
            scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true)
            scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)
            scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true)

            startActivityForResult(context as BaseActivity, scanIntent, SCAN_REQUEST_CODE, null)
        }
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

    fun getCardNumber(): String {
        return etCardNumber.text.toString().replace(" ", "")
    }

    fun setCardNumber(cardNumber: String) {
        etCardNumber.setText(cardNumber, TextView.BufferType.EDITABLE)
    }

    override fun setEnabled(enabled: Boolean) {
        etCardNumber.isEnabled = enabled
        btnScan.isEnabled = enabled

        super.setEnabled(enabled)
    }

    fun setIconColor(@ColorRes iconColor: Int) {
        ImageViewCompat.setImageTintList(
            btnScan,
            ColorStateList.valueOf(ContextCompat.getColor(context, iconColor))
        )
    }

}