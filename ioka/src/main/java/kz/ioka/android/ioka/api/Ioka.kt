package kz.ioka.android.ioka.api

import android.app.Activity
import android.content.Intent
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.presentation.flows.payment.PaymentLauncherBehavior
import kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withCvv.CvvPaymentLauncherBehavior
import kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withoutCvv.PayWithCardIdActivity
import kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withoutCvv.PayWithCardIdLauncher
import kz.ioka.android.ioka.presentation.flows.saveCard.SaveCardActivity
import kz.ioka.android.ioka.presentation.flows.saveCard.SaveCardLauncher
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherActivity
import kz.ioka.android.ioka.util.getCustomerId
import kz.ioka.android.ioka.viewBase.BaseActivity
import java.net.ProtocolException

object Ioka {

    private val cardApi by lazy { DependencyInjector.cardApi }

    fun init(apiKey: String) {
        Config.apiKey = apiKey

        DependencyInjector.createDependencies()
    }

    // TODO Implement Google Pay
    fun startPaymentFlow(
        orderToken: String,
        configuration: Configuration? = null
    ): (Activity) -> Unit {
        if (Config.isApiKeyInitialized().not()) {
            throw RuntimeException("Init Ioka with your API_KEY")
        }

        return {
            val intent = PaymentLauncherActivity.provideIntent(
                it,
                PaymentLauncherBehavior(
                    orderToken,
                    false,
                    configuration
                )
            )

            it.startActivity(intent)
        }
    }

    fun startPaymentWithSavedCardFlow(
        orderToken: String,
        card: CardDvo,
        configuration: Configuration? = null
    ): (Activity) -> Unit {
        if (card.cvvRequired) {
            return { activity ->
                val intent = PaymentLauncherActivity.provideIntent(
                    activity,
                    CvvPaymentLauncherBehavior(
                        orderToken,
                        card.cardId,
                        card.cardNumber,
                        card.cardType,
                        configuration
                    )
                )

                activity.startActivity(intent)
            }
        } else {
            return {
                val intent = PayWithCardIdActivity.provideIntent(
                    it,
                    PayWithCardIdLauncher(
                        orderToken,
                        card.cardId
                    )
                )

                it.startActivity(intent)
            }
        }
    }

    fun startSaveCardFlow(
        customerToken: String,
        configuration: Configuration? = null
    ): (Activity) -> Unit {
        if (Config.isApiKeyInitialized().not()) {
            throw RuntimeException("Init Ioka with your API_KEY")
        }

        return { activity ->
            val intent = Intent(activity, SaveCardActivity::class.java)
            intent.putExtra(
                BaseActivity.LAUNCHER,
                SaveCardLauncher(customerToken, configuration)
            )

            activity.startActivity(intent)
        }
    }

    suspend fun getCards(customerToken: String): List<CardModel> {
        if (Config.isApiKeyInitialized().not()) {
            throw RuntimeException("Init Ioka with your API_KEY")
        }

        return cardApi.getCards(
            Config.apiKey, customerToken, customerToken.getCustomerId()
        ).map {
            CardModel(
                id = it.id,
                customerId = it.customer_id,
                createdAt = it.created_at,
                panMasked = it.pan_masked,
                expiryDate = it.expiry_date,
                holder = it.holder,
                paymentSystem = it.payment_system,
                emitter = it.emitter,
                cvcRequired = it.cvc_required,
            )
        }
    }

    suspend fun removeCard(
        customerToken: String,
        cardId: String
    ): Boolean {
        if (Config.isApiKeyInitialized().not()) {
            throw RuntimeException("Init Ioka with your API_KEY")
        }

        return try {
            cardApi.removeCard(
                Config.apiKey,
                customerToken,
                customerToken.getCustomerId(),
                cardId
            )
            true
        } catch (e: Exception) {
            e is ProtocolException
        }
    }

}