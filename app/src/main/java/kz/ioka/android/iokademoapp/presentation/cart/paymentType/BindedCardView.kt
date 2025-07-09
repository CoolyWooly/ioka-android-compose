package kz.ioka.android.iokademoapp.presentation.cart.paymentType

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import kz.ioka.android.R
import kz.ioka.android.iokademoapp.common.shortPanMask
import kz.ioka.android.iokademoapp.common.toPx
import kz.ioka.android.iokademoapp.presentation.profile.savedCards.CardDvo

class BindedCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private lateinit var ivCardType: AppCompatImageView
    private lateinit var tvCardPan: AppCompatTextView
    lateinit var ivCheck: AppCompatImageView

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.view_binded_card, this, true)

        setupView()
        bindViews(root)
    }

    private fun setupView() {
        gravity = Gravity.CENTER_VERTICAL
        orientation = HORIZONTAL
        setPadding(16.toPx.toInt())
    }

    private fun bindViews(root: View) {
        ivCardType = root.findViewById(R.id.ivCardType)
        tvCardPan = root.findViewById(R.id.tvCardPan)
        ivCheck = root.findViewById(R.id.ivCheck)
    }

    fun setCard(card: CardDvo) {
        ivCardType.setImageDrawable(ContextCompat.getDrawable(context, card.cardType))
        tvCardPan.text = card.cardPan.shortPanMask()
    }

}