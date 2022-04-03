package kz.ioka.android.ioka.presentation.flows.payWithCardId

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.order.OrderRepositoryImpl
import kz.ioka.android.ioka.domain.payment.PaymentRepositoryImpl
import kz.ioka.android.ioka.presentation.flows.common.PaymentState
import kz.ioka.android.ioka.presentation.result.ErrorResultLauncher
import kz.ioka.android.ioka.presentation.result.ResultActivity
import kz.ioka.android.ioka.presentation.result.SuccessResultLauncher
import kz.ioka.android.ioka.presentation.webView.PaymentConfirmationBehavior
import kz.ioka.android.ioka.presentation.webView.WebViewActivity
import kz.ioka.android.ioka.uikit.ButtonState
import kz.ioka.android.ioka.util.getOrderId
import kz.ioka.android.ioka.util.showErrorToast
import kz.ioka.android.ioka.viewBase.BaseActivity
import java.math.BigDecimal

internal class PayWithCardIdActivity : BaseActivity() {

    companion object {
        fun provideIntent(context: Context, launcher: PayWithCardIdLauncher): Intent {
            val intent = Intent(context, PayWithCardIdActivity::class.java)
            intent.putExtra(LAUNCHER, launcher)

            return intent
        }
    }

    private val viewModel: PayWithCardIdViewModel by viewModels {
        PayWithCardIdViewModelFactory(
            launcher()!!,
            OrderRepositoryImpl(DependencyInjector.orderApi),
            PaymentRepositoryImpl(DependencyInjector.paymentApi)
        )
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                onSuccessfulPayment()
            } else if (it.resultCode == RESULT_CANCELED) {
                onFailedPayment(getString(R.string.ioka_result_failed_payment_common_cause))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_with_card_id)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.apply {
            payState.observe(this@PayWithCardIdActivity) {
                handleState(it)
            }
        }
    }

    private fun handleState(state: PaymentState) {
        when (state) {
            is PaymentState.PENDING -> {
                on3DSecureNeeded(state.actionUrl)
            }

            PaymentState.SUCCESS -> {
                onSuccessfulPayment()
            }

            is PaymentState.ERROR -> {
                showErrorToast(state.cause ?: getString(R.string.ioka_common_server_error))
                finish()
            }

            is PaymentState.FAILED -> {
                onFailedPayment(
                    state.cause ?: getString(R.string.ioka_result_failed_payment_common_cause)
                )
            }
        }
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
                    viewModel.order.externalId
                ),
                amount = viewModel.order.amount
            )
        )

        startActivity(intent)
    }

    private fun onFailedPayment(cause: String) {
        finish()

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(
            LAUNCHER,
            ErrorResultLauncher(subtitle = cause)
        )

        startActivity(intent)
    }

}