package kz.ioka.android.ioka

import android.content.Context
import android.content.Intent
import kz.ioka.android.ioka.presentation.flows.bindCard.BindCardActivity
import kz.ioka.android.ioka.presentation.flows.bindCard.BindCardLauncher
import kz.ioka.android.ioka.presentation.flows.payWithCard.PayWithCardActivity
import kz.ioka.android.ioka.presentation.flows.payWithCard.PayWithCardLauncher
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class FormFactory {

    fun provideIntent(apiKey: String, paymentFlow: PaymentFlow, context: Context): Intent {
        return when (paymentFlow) {
            is PaymentFlow.BindCardFlow -> {
                val intent = Intent(context, BindCardActivity::class.java)
                intent.putExtra(
                    BaseActivity.LAUNCHER,
                    BindCardLauncher(apiKey, paymentFlow.customerToken)
                )
                intent
            }

            is PaymentFlow.PayWithCardFlow -> {
                val intent = Intent(context, PayWithCardActivity::class.java)
                intent.putExtra(
                    BaseActivity.LAUNCHER,
                    PayWithCardLauncher(
                        apiKey,
                        paymentFlow.customerToken,
                        paymentFlow.orderToken,
                        paymentFlow.price,
                        paymentFlow.withGooglePay
                    )
                )
                intent
            }
        }
    }

}