package kz.ioka.android.iokademoapp.presentation.cart.paymentType

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kz.ioka.android.iokademoapp.BaseActivity
import kz.ioka.android.iokademoapp.R
import kz.ioka.android.iokademoapp.common.toCardType
import kz.ioka.android.iokademoapp.common.toPx
import kz.ioka.android.iokademoapp.presentation.cart.PaymentTypeDvo
import kz.ioka.android.iokademoapp.presentation.profile.savedCards.CardDvo

@AndroidEntryPoint
class SelectPaymentTypeActivity : BaseActivity() {

    companion object {
        const val LAUNCHER = "SelectPaymentTypeActivity_LAUNCHER"
    }

    private val viewModel: SelectPaymentTypeViewModel by viewModels()

    private lateinit var vToolbar: MaterialToolbar
    private lateinit var vCardContainer: LinearLayoutCompat
    private lateinit var btnGooglePay: LinearLayoutCompat
    private lateinit var ivCheckGooglePay: AppCompatImageView
    private lateinit var btnPayWithCard: LinearLayoutCompat
    private lateinit var ivCheckPayWithCard: AppCompatImageView
    private lateinit var btnPayWithCash: LinearLayoutCompat
    private lateinit var ivCheckCash: AppCompatImageView
    private lateinit var btnSave: AppCompatButton

    private var selectedPaymentTypeCheck: AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_payment_type)

        bindViews()
        observeData()
        setListeners()
    }

    private fun bindViews() {
        vToolbar = findViewById(R.id.vToolbar)
        vCardContainer = findViewById(R.id.vCardContainer)
        btnGooglePay = findViewById(R.id.btnGooglePay)
        ivCheckGooglePay = findViewById(R.id.ivCheckGooglePay)
        btnPayWithCard = findViewById(R.id.btnPayWithCard)
        ivCheckPayWithCard = findViewById(R.id.ivCheckPayWithCard)
        btnPayWithCash = findViewById(R.id.btnPayWithCash)
        ivCheckCash = findViewById(R.id.ivCheckCash)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun observeData() {
        viewModel.apply {
            selectedPaymentType.observe(this@SelectPaymentTypeActivity) {
                if (it.isPresent())
                    setSelectedPaymentType(it.get())
            }

            bindedCards.observe(this@SelectPaymentTypeActivity) {
                it.forEach { card ->
                    val cardView = BindedCardView(this@SelectPaymentTypeActivity)
                    cardView.setCard(CardDvo(card.id!!, R.drawable.ic_ps_visa, card.panMasked!!))

                    val dividerView = View(this@SelectPaymentTypeActivity)
                    dividerView.layoutParams =
                        LinearLayoutCompat.LayoutParams(MATCH_PARENT, 1.toPx.toInt())
                    (dividerView.layoutParams as LinearLayoutCompat.LayoutParams).marginStart =
                        52.toPx.toInt()
                    dividerView.setBackgroundColor(
                        ContextCompat.getColor(
                            this@SelectPaymentTypeActivity, R.color.ioka_color_divider
                        )
                    )

                    vCardContainer.addView(dividerView)
                    vCardContainer.addView(cardView)

                    cardView.setOnClickListener {
                        viewModel.onPaymentTypeSelected(
                            PaymentTypeDvo.PayWithSavedCardDvo(
                                card.id ?: "",
                                card.panMasked ?: "",
                                card.paymentSystem.toCardType(),
                                card.cvcRequired ?: true
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setSelectedPaymentType(paymentType: PaymentTypeDvo) {
        when (paymentType) {
            PaymentTypeDvo.GooglePayDvo -> {
                ivCheckGooglePay.isVisible = true
                selectedPaymentTypeCheck?.isVisible = false
                selectedPaymentTypeCheck = ivCheckGooglePay
            }
            PaymentTypeDvo.PayWithCardDvo -> {
                ivCheckPayWithCard.isVisible = true
                selectedPaymentTypeCheck?.isVisible = false
                selectedPaymentTypeCheck = ivCheckPayWithCard
            }
            PaymentTypeDvo.PayWithCashDvo -> {
                ivCheckCash.isVisible = true
                selectedPaymentTypeCheck?.isVisible = false
                selectedPaymentTypeCheck = ivCheckCash
            }
            is PaymentTypeDvo.PayWithSavedCardDvo -> {
                selectedPaymentTypeCheck?.isVisible = false
                selectedPaymentTypeCheck = null
            }
        }
    }

    private fun setListeners() {
        vToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        btnGooglePay.setOnClickListener {
            viewModel.onPaymentTypeSelected(PaymentTypeDvo.GooglePayDvo)
        }

        btnPayWithCard.setOnClickListener {
            viewModel.onPaymentTypeSelected(PaymentTypeDvo.PayWithCardDvo)
        }

        btnPayWithCash.setOnClickListener {
            viewModel.onPaymentTypeSelected(PaymentTypeDvo.PayWithCashDvo)
        }

        btnSave.setOnClickListener {
            if (viewModel.selectedPaymentType.value?.isPresent() == true) {
                val intent = intent
                intent.putExtra(LAUNCHER, viewModel.selectedPaymentType.value?.get())

                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

}