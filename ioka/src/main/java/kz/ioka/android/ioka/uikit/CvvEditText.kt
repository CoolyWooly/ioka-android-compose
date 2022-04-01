package kz.ioka.android.ioka.uikit

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import io.card.payment.CardIOActivity
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.util.textChanges
import kz.ioka.android.ioka.util.toPx
import kz.ioka.android.ioka.viewBase.BaseActivity

class CvvEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private lateinit var etCvv: AppCompatEditText
    private lateinit var ivCvvFaq: AppCompatImageView

    var onTextChanged: (String) -> Unit = {}
    var onFaqClicked: () -> Unit = {}

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.view_cvv_input, this, true)

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
        background = AppCompatResources.getDrawable(context, R.drawable.bg_edittext)
        gravity = Gravity.CENTER_VERTICAL
    }

    private fun setupListeners() {
        etCvv.doOnTextChanged { text, _, _, _ ->
            onTextChanged(text.toString().replace(" ", ""))
        }

        etCvv.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            val strokeWidth = if (hasFocus) 1.toPx.toInt() else 0

            val back = background as GradientDrawable

            back.mutate()
            back.setStroke(
                strokeWidth,
                ContextCompat.getColor(context, R.color.ioka_color_primary)
            )

            background = back
        }

        ivCvvFaq.setOnClickListener {
            onFaqClicked()
        }
    }

    fun getCvv(): String {
        return etCvv.text.toString()
    }

    fun setTypeface(typeface: Typeface) {
        etCvv.typeface = typeface
    }

    fun setRadius(toPx: Float) {
        (background as GradientDrawable).cornerRadius = toPx
    }

}