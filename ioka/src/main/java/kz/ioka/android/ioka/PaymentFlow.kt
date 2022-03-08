package kz.ioka.android.ioka

sealed interface PaymentFlow {

    class SaveCardFlow(
        val customerToken: String
    ) : PaymentFlow

    class PayWithCardFlow(
        val customerToken: String,
        val orderToken: String,
        val price: Int
    ) : PaymentFlow

}