package kz.ioka.android.ioka.presentation.flows.payment

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.cardInfo.CardInfoRepositoryImpl
import kz.ioka.android.ioka.domain.payment.PaymentRepositoryImpl
import kz.ioka.android.ioka.presentation.flows.common.CardInfoViewModel
import kz.ioka.android.ioka.presentation.flows.common.CardInfoViewModelFactory
import kz.ioka.android.ioka.presentation.flows.common.PaymentState
import kz.ioka.android.ioka.presentation.result.ErrorResultLauncher
import kz.ioka.android.ioka.presentation.result.ResultActivity
import kz.ioka.android.ioka.presentation.result.SuccessResultLauncher
import kz.ioka.android.ioka.uikit.ButtonState
import kz.ioka.android.ioka.uikit.CardNumberEditText
import kz.ioka.android.ioka.uikit.CvvEditText
import kz.ioka.android.ioka.uikit.IokaStateButton
import kz.ioka.android.ioka.util.showErrorToast
import kz.ioka.android.ioka.util.toAmountFormat
import kz.ioka.android.ioka.viewBase.BasePaymentActivity
import kz.ioka.android.ioka.viewBase.Scannable

internal class PayActivity : BasePaymentActivity(), Scannable {

    private val cardInfoViewModel: CardInfoViewModel by viewModels {
        CardInfoViewModelFactory(
            CardInfoRepositoryImpl(DependencyInjector.cardInfoApi)
        )
    }
    private val viewModel: PayWithCardViewModel by viewModels {
        PayWithCardViewModelFactory(
            launcher()!!,
            PaymentRepositoryImpl(DependencyInjector.paymentApi)
        )
    }

    private lateinit var vRoot: ConstraintLayout
    private lateinit var vToolbar: Toolbar
    private lateinit var groupGooglePay: Group
    private lateinit var btnGooglePay: AppCompatImageButton
    private lateinit var etCardNumber: CardNumberEditText
    private lateinit var etExpireDate: AppCompatEditText
    private lateinit var vCvvInput: CvvEditText
    private lateinit var switchSaveCard: SwitchCompat
    private lateinit var btnPay: IokaStateButton

    override fun onCardScanned(cardNumber: String) {
        etCardNumber.setCardNumber(cardNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ioka_activity_pay)

        bindViews()
        setConfiguration()
        setupListeners()
        observeData()
    }

    private fun bindViews() {
        vRoot = findViewById(R.id.vRoot)
        vToolbar = findViewById(R.id.vToolbar)
        groupGooglePay = findViewById(R.id.groupGooglePay)
        btnGooglePay = findViewById(R.id.btnGooglePay)
        etCardNumber = findViewById(R.id.vCardNumberInput)
        etExpireDate = findViewById(R.id.etExpireDate)
        vCvvInput = findViewById(R.id.vCvvInput)
        switchSaveCard = findViewById(R.id.vSaveCardSwitch)
        btnPay = findViewById(R.id.btnPay)
    }

    private fun setConfiguration() {
        launcher<PayLauncher>()?.configuration?.apply {
            vRoot.setBackgroundColor(
                ContextCompat.getColor(this@PayActivity, backgroundColor)
            )

            etCardNumber.setIconColor(iconColor)
            vCvvInput.setIconColor(iconColor)

            buttonText?.let { btnPay.setText(buttonText) }

            fieldBackground?.let {
                etCardNumber.background = ContextCompat.getDrawable(this@PayActivity, it)
                etExpireDate.background = ContextCompat.getDrawable(this@PayActivity, it)
                vCvvInput.background = ContextCompat.getDrawable(this@PayActivity, it)
            }
            buttonBackground?.let {
                btnPay.background = ContextCompat.getDrawable(this@PayActivity, it)
            }
        }
    }

    private fun setupListeners() {
        vToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        etCardNumber.onTextChangedWithDebounce = {
            cardInfoViewModel.onCardPanEntered(it)
        }
        etCardNumber.flowTextChangedWithDebounce.launchIn(lifecycleScope)

        etCardNumber.onTextChanged = {
            viewModel.onCardPanEntered(it)
        }

        etExpireDate.doOnTextChanged { text, _, _, _ ->
            viewModel.onExpireDateEntered(text.toString().replace("/", ""))
        }

        vCvvInput.onTextChanged = {
            viewModel.onCvvEntered(it)
        }

        btnPay.setOnClickListener {
            viewModel.onPayClicked(
                etCardNumber.getCardNumber(),
                etExpireDate.text.toString(),
                vCvvInput.getCvv(),
                switchSaveCard.isChecked
            )
        }
    }

    private fun observeData() {
        viewModel.apply {
            vToolbar.title =
                getString(R.string.ioka_payment_toolbar, order.amount.amount.toAmountFormat())
            btnPay.setText(
                getString(
                    R.string.ioka_payment_button,
                    order.amount.amount.toAmountFormat()
                )
            )
            groupGooglePay.isVisible = withGooglePay
            switchSaveCard.isVisible = canSaveCard

            payState.observe(this@PayActivity) {
                handleState(it)
            }
        }
    }

    private fun handleState(state: PaymentState) {
        when (state) {
            PaymentState.LOADING -> {
                btnPay.setState(ButtonState.Loading)
                disableInputs()
            }

            PaymentState.DISABLED -> {
                btnPay.setState(ButtonState.Disabled)
            }

            is PaymentState.PENDING -> {
                btnPay.setState(ButtonState.Default)

                onActionRequired(
                    state.actionUrl,
                    viewModel.orderToken,
                    viewModel.paymentId
                )
            }

            PaymentState.SUCCESS -> {
                btnPay.setState(ButtonState.Default)

                onSuccessfulPayment()
            }

            is PaymentState.ERROR -> {
                btnPay.setState(ButtonState.Default)

                showErrorToast(state.cause ?: getString(R.string.ioka_common_server_error))
            }

            is PaymentState.FAILED -> {
                btnPay.setState(ButtonState.Default)

                onFailedPayment(
                    state.cause ?: getString(R.string.ioka_result_failed_payment_common_cause)
                )
            }

            PaymentState.DEFAULT -> {
                btnPay.setState(ButtonState.Default)
                enableInputs()
            }
        }
    }

    private fun enableInputs() {
        etCardNumber.isEnabled = true
        vCvvInput.isEnabled = true
        etExpireDate.isEnabled = true
        switchSaveCard.isEnabled = true
        btnGooglePay.isEnabled = true
    }

    private fun disableInputs() {
        etCardNumber.isEnabled = false
        vCvvInput.isEnabled = false
        etExpireDate.isEnabled = false
        switchSaveCard.isEnabled = false
        btnGooglePay.isEnabled = false
    }

    override fun onSuccessfulPayment() {
        finish()

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(
            LAUNCHER,
            SuccessResultLauncher(
                subtitle = getString(
                    R.string.ioka_result_success_payment_subtitle,
                    viewModel.order.externalId
                ),
                amount = viewModel.order.amount
            )
        )

        startActivity(intent)
    }

    override fun onFailedPayment(cause: String?) {
        finish()

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(
            LAUNCHER,
            ErrorResultLauncher(
                subtitle = cause ?: getString(R.string.ioka_result_failed_payment_common_cause)
            )
        )

        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<BasePaymentActivity>.onActivityResult(requestCode, resultCode, data)
        super<Scannable>.onActivityResult(requestCode, resultCode, data)
    }

}