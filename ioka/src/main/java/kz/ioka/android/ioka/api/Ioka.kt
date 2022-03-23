package kz.ioka.android.ioka.api

import androidx.appcompat.app.AppCompatActivity
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.di.DependencyInjector

object Ioka {

    private val formFactory = FormFactory()

    fun init(apiKey: String) {
        Config.apiKey = apiKey

        DependencyInjector.createDependencies()
    }

    fun showForm(paymentFlow: PaymentFlow): (AppCompatActivity) -> Unit {
        if (Config.apiKey == null) {
            throw RuntimeException("Init Ioka with your API_KEY")
        }

        return { context ->
            formFactory.provideAction(Config.apiKey!!, paymentFlow, context).invoke(context)
        }
    }

}