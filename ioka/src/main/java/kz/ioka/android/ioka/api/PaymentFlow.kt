package kz.ioka.android.ioka.api

sealed interface PaymentFlow {

    class BindCardFlow(
        val customerToken: String
    ) : PaymentFlow

    class PayWithCardFlow(
        val orderToken: String,
        val withGooglePay: Boolean
    ) : PaymentFlow

    class PayWithBindedCardFlow(
        val orderToken: String,
        val cardId: String,
        val cardNumber: String,
        val cardType: String,
        val cvvRequired: Boolean,
    ) : PaymentFlow

}