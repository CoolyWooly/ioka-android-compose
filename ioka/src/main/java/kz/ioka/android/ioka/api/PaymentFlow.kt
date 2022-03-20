package kz.ioka.android.ioka.api

sealed interface PaymentFlow {

    class BindCardFlow(
        val customerToken: String
    ) : PaymentFlow

    class PayWithCardFlow(
        val customerToken: String,
        val orderToken: String,
        val price: Int,
        val withGooglePay: Boolean
    ) : PaymentFlow

}