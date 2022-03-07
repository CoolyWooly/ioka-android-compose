package kz.ioka.android.ioka

import android.content.Context

class Ioka {

    private val formFactory = FormFactory()

    private lateinit var apiKey: String
    private lateinit var paymentFlow: PaymentFlow

    fun setup(apiKey: String, paymentFlow: PaymentFlow) {
        this.apiKey = apiKey
        this.paymentFlow = paymentFlow
    }

    fun showForm(): (Context) -> Unit {
        return { context ->
            context.startActivity(formFactory.provideIntent(apiKey, paymentFlow, context))
        }
    }

}