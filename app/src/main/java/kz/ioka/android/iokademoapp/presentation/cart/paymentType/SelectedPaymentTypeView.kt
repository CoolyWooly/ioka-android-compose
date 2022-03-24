package kz.ioka.android.iokademoapp.presentation.cart.paymentType

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import kz.ioka.android.iokademoapp.R
import kz.ioka.android.iokademoapp.common.toPx
import kz.ioka.android.iokademoapp.presentation.cart.PaymentTypeDvo

class SelectedPaymentTypeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private lateinit var ivPaymentIcon: AppCompatImageView
    private lateinit var tvPaymentType: AppCompatTextView

    init {
        val root =
            LayoutInflater.from(context).inflate(R.layout.view_selected_payment_type, this, true)

        setupView()
        bindViews(root)
    }

    private fun setupView() {
        radius = 8.toPx
        cardElevation = 0.toPx
    }

    private fun bindViews(root: View) {
        ivPaymentIcon = root.findViewById(R.id.ivPaymentIcon)
        tvPaymentType = root.findViewById(R.id.tvPaymentType)
    }

    fun setPaymentType(paymentType: PaymentTypeDvo) {
        when (paymentType) {
            PaymentTypeDvo.GooglePayDvo -> {
                ivPaymentIcon.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_google_pay_mark)
                )
                tvPaymentType.setText(R.string.order_details_payment_type_google_pay)
            }
            PaymentTypeDvo.PayWithCashDvo -> {
                ivPaymentIcon.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_cash)
                )
                tvPaymentType.setText(R.string.order_details_payment_type_with_cash)
            }
            PaymentTypeDvo.PayWithCardDvo -> {
                ivPaymentIcon.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_card)
                )
                tvPaymentType.setText(R.string.order_details_payment_type_with_card)
            }
            is PaymentTypeDvo.PayWithSavedCardDvo -> {
                ivPaymentIcon.setImageDrawable(
                    ContextCompat.getDrawable(context, paymentType.cardType.cardTypeRes)
                )
                tvPaymentType.text = paymentType.maskedCardNumber
            }
        }
    }

}