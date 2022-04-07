package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withoutCvv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.order.OrderRepositoryImpl
import kz.ioka.android.ioka.domain.payment.PaymentRepositoryImpl
import kz.ioka.android.ioka.presentation.flows.common.PaymentState
import kz.ioka.android.ioka.presentation.result.ErrorResultLauncher
import kz.ioka.android.ioka.presentation.result.ResultActivity
import kz.ioka.android.ioka.presentation.result.SuccessResultLauncher
import kz.ioka.android.ioka.util.showErrorToast
import kz.ioka.android.ioka.viewBase.BasePaymentActivity

internal class PayWithCardIdActivity : BasePaymentActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ioka_activity_pay_with_card_id)

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
                onActionRequired(
                    state.actionUrl,
                    viewModel.orderToken,
                    viewModel.paymentId
                )
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
            ErrorResultLauncher(subtitle = cause ?: getString(R.string.ioka_common_server_error))
        )

        startActivity(intent)
    }

}