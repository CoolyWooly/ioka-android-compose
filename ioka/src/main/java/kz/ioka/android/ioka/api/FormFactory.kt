package kz.ioka.android.ioka.api

import android.content.Context
import android.content.Intent
import kz.ioka.android.ioka.presentation.flows.bindCard.BindCardActivity
import kz.ioka.android.ioka.presentation.flows.bindCard.BindCardLauncher
import kz.ioka.android.ioka.presentation.flows.payWithBindedCard.CvvFragment
import kz.ioka.android.ioka.presentation.flows.payWithBindedCard.CvvLauncher
import kz.ioka.android.ioka.presentation.flows.payWithCard.PayWithCardActivity
import kz.ioka.android.ioka.presentation.flows.payWithCard.PayWithCardLauncher
import kz.ioka.android.ioka.util.ViewAction
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class FormFactory {

    fun provideAction(
        apiKey: String,
        paymentFlow: PaymentFlow,
        context: Context
    ): ViewAction {
        return when (paymentFlow) {
            is PaymentFlow.BindCardFlow -> ViewAction {
                val intent = Intent(context, BindCardActivity::class.java)
                intent.putExtra(
                    BaseActivity.LAUNCHER,
                    BindCardLauncher(apiKey, paymentFlow.customerToken)
                )
                it.startActivity(intent)
            }
            is PaymentFlow.PayWithCardFlow -> ViewAction {
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
                it.startActivity(intent)
            }
            is PaymentFlow.PayWithBindedCardFlow -> ViewAction {
                if (paymentFlow.cvvRequired) {
                    val newFragment: CvvFragment = CvvFragment.newInstance(
                        CvvLauncher(
                            paymentFlow.customerToken,
                            paymentFlow.orderToken,
                            paymentFlow.price,
                            paymentFlow.cardId,
                            paymentFlow.cardNumber,
                            paymentFlow.cardType,
                            paymentFlow.cvvRequired
                        )
                    )
                    newFragment.show(it.supportFragmentManager, newFragment::class.simpleName)
                }
            }
        }
    }

}