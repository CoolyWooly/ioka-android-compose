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
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.util.getPrimaryColor
import kz.ioka.android.ioka.util.toPx


internal class CvvEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private lateinit var etCvv: AppCompatEditText
    lateinit var ivCvvFaq: AppCompatImageView

    var onTextChanged: (String) -> Unit = {}
    var onFaqClicked: () -> Unit = {}

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
            onTextChanged(text.toString().replace(" ", ""))
        }

        etCvv.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            val strokeWidth = if (hasFocus) 1.toPx.toInt() else 0

            val back = background as GradientDrawable

            back.mutate()
            back.setStroke(
                strokeWidth,
                context.getPrimaryColor()
            )

            background = back
        }

        ivCvvFaq.setOnClickListener {
            onFaqClicked()
        }
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

    override fun setEnabled(enabled: Boolean) {
        etCvv.isEnabled = enabled
        ivCvvFaq.isEnabled = enabled

        super.setEnabled(enabled)
    }

}