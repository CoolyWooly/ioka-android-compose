package kz.ioka.android.ioka.presentation.flows.payWithCard

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
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
import kz.ioka.android.ioka.presentation.result.ErrorResultLauncher
import kz.ioka.android.ioka.presentation.result.ResultActivity
import kz.ioka.android.ioka.presentation.result.SuccessResultLauncher
import kz.ioka.android.ioka.presentation.webView.PaymentConfirmationBehavior
import kz.ioka.android.ioka.presentation.webView.WebViewActivity
import kz.ioka.android.ioka.uikit.ButtonState
import kz.ioka.android.ioka.uikit.CardNumberEditText
import kz.ioka.android.ioka.uikit.IokaStateButton
import kz.ioka.android.ioka.util.getOrderId
import kz.ioka.android.ioka.util.showErrorToast
import kz.ioka.android.ioka.util.toAmountFormat
import kz.ioka.android.ioka.viewBase.BaseActivity
import java.math.BigDecimal

internal class PayWithCardActivity : BaseActivity() {

    private var launcher: PayWithCardLauncher? = null

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
    private lateinit var etCvv: AppCompatEditText
    private lateinit var switchBindCard: SwitchCompat
    private lateinit var btnPay: IokaStateButton

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                onSuccessfulPayment()
            } else if (it.resultCode == RESULT_CANCELED) {
                onFailedPayment(getString(R.string.ioka_result_failed_payment_common_cause))
            }
        }

    override fun onCardScanned(cardNumber: String) {
        etCardNumber.setCardNumber(cardNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_with_card)

        launcher = launcher()
        bindViews()
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
        etCvv = findViewById(R.id.etCvv)
        switchBindCard = findViewById(R.id.vBindCardSwitch)
        btnPay = findViewById(R.id.btnPay)
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

        etCvv.doOnTextChanged { text, _, _, _ ->
            viewModel.onCvvEntered(text.toString())
        }

        btnPay.setOnClickListener {
            viewModel.onPayClicked(
                etCardNumber.getCardNumber(),
                etExpireDate.text.toString(),
                etCvv.text.toString(),
                switchBindCard.isChecked
            )
        }
    }

    private fun observeData() {
        viewModel.apply {
            vToolbar.title = getString(R.string.ioka_payment_toolbar, price.toAmountFormat())
            btnPay.setText(getString(R.string.ioka_payment_button, price.toAmountFormat()))
            groupGooglePay.isVisible = withGooglePay

            payState.observe(this@PayWithCardActivity) {
                handleState(it)
            }
        }
    }

    private fun handleState(state: PayState) {
        when (state) {
            PayState.LOADING -> {
                btnPay.setState(ButtonState.Loading)
                disableInputs()
            }

            PayState.DISABLED -> {
                btnPay.setState(ButtonState.Disabled)
            }

            is PayState.PENDING -> {
                btnPay.setState(ButtonState.Default)

                on3DSecureNeeded(state.actionUrl)
            }

            PayState.SUCCESS -> {
                btnPay.setState(ButtonState.Default)

                onSuccessfulPayment()
            }

            is PayState.ERROR -> {
                btnPay.setState(ButtonState.Default)

                showErrorToast(state.cause ?: getString(R.string.ioka_common_server_error))
            }

            is PayState.FAILED -> {
                btnPay.setState(ButtonState.Default)

                onFailedPayment(
                    state.cause ?: getString(R.string.ioka_result_failed_payment_common_cause)
                )
            }

            PayState.DEFAULT -> {
                btnPay.setState(ButtonState.Default)
                enableInputs()
            }
        }
    }

    private fun enableInputs() {
        etCardNumber.isEnabled = true
        etCvv.isEnabled = true
        etExpireDate.isEnabled = true
        switchBindCard.isEnabled = true
        btnGooglePay.isEnabled = true
    }

    private fun disableInputs() {
        etCardNumber.isEnabled = false
        etCvv.isEnabled = false
        etExpireDate.isEnabled = false
        switchBindCard.isEnabled = false
        btnGooglePay.isEnabled = false
    }

    private fun on3DSecureNeeded(actionUrl: String) {
        val intent = WebViewActivity.provideIntent(
            this, PaymentConfirmationBehavior(
                url = actionUrl,
                customerToken = viewModel.customerToken,
                orderToken = viewModel.orderToken,
                paymentId = viewModel.paymentId
            )
        )

        startForResult.launch(intent)
    }

    private fun onSuccessfulPayment() {
        finish()

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(
            LAUNCHER,
            SuccessResultLauncher(
                subtitle = getString(
                    R.string.ioka_result_success_payment_subtitle,
                    launcher?.orderToken?.getOrderId()
                ),
                amount = launcher?.price ?: BigDecimal.ZERO
            )
        )

        startActivity(intent)
    }

    private fun onFailedPayment(cause: String) {
        finish()

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(
            LAUNCHER,
            ErrorResultLauncher(subtitle = cause, amount = BigDecimal.ZERO)
        )

        startActivity(intent)
    }

}