package kz.ioka.android.ioka.api

import android.content.Context
import android.content.Intent
import kz.ioka.android.ioka.presentation.flows.payWithBindedCard.CvvPaymentLauncherBehavior
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherActivity
import kz.ioka.android.ioka.presentation.flows.payWithCard.PayWithCardLauncherBehavior
import kz.ioka.android.ioka.presentation.flows.bindCard.BindCardActivity
import kz.ioka.android.ioka.presentation.flows.bindCard.BindCardLauncher
import kz.ioka.android.ioka.presentation.flows.payWithCardId.PayWithCardIdActivity
import kz.ioka.android.ioka.presentation.flows.payWithCardId.PayWithCardIdLauncher
import kz.ioka.android.ioka.util.ViewAction
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class FormFactory {

    fun provideAction(
        paymentFlow: PaymentFlow,
        context: Context
    ): ViewAction {
        return when (paymentFlow) {
            is PaymentFlow.BindCardFlow -> ViewAction {
                val intent = Intent(context, BindCardActivity::class.java)
                intent.putExtra(
                    BaseActivity.LAUNCHER,
                    BindCardLauncher(paymentFlow.customerToken)
                )
                it.startActivity(intent)
            }
            is PaymentFlow.PayWithCardFlow -> ViewAction {
                val intent = PaymentLauncherActivity.provideIntent(
                    it,
                    PayWithCardLauncherBehavior(
                        paymentFlow.customerToken,
                        paymentFlow.orderToken,
                        paymentFlow.withGooglePay
                    )
                )

                it.startActivity(intent)
            }
            is PaymentFlow.PayWithBindedCardFlow ->
                if (paymentFlow.cvvRequired) {
                    ViewAction { activity ->
                        val intent = PaymentLauncherActivity.provideIntent(
                            activity,
                            CvvPaymentLauncherBehavior(
                                paymentFlow.customerToken,
                                paymentFlow.orderToken,
                                paymentFlow.cardId,
                                paymentFlow.cardNumber,
                                paymentFlow.cardType,
                            )
                        )

                        activity.startActivity(intent)
                    }
                } else {
                    ViewAction {
                        val intent = PayWithCardIdActivity.provideIntent(
                            it,
                            PayWithCardIdLauncher(
                                paymentFlow.customerToken,
                                paymentFlow.orderToken,
                                paymentFlow.cardId
                            )
                        )

                        it.startActivity(intent)
                    }
                }
        }

    }

}