package kz.ioka.android.ioka.domain.payment

internal sealed class PaymentModel {

    companion object {
        // Нужно подтверждение 3DSecure
        const val REQUIRES_ACTION = "REQUIRES_ACTION"

        // Платеж принят в обработку (успешный кейс)
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_CAPTURED = "CAPTURED"

        // Платеж отклонен
        const val STATUS_CANCELLED = "CANCELLED"
        const val STATUS_DECLINED = "DECLINED"
    }

    object Success : PaymentModel()

    class Declined(
        val code: String, val message: String
    ) : PaymentModel()

    class Pending(
        val paymentId: String,
        val actionUrl: String
    ) : PaymentModel()

}