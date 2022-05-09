package kz.ioka.android.ioka.domain.payment

import kotlinx.coroutines.Dispatchers
import kz.ioka.android.ioka.data.payment.PaymentApi
import kz.ioka.android.ioka.data.payment.PaymentRequestDto
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.errorHandler.safeApiCall
import kz.ioka.android.ioka.util.getOrderId

internal interface PaymentRepository {

    suspend fun createCardPayment(
        orderId: String,
        apiKey: String,
        panNumber: String,
        expiryDate: String,
        cvv: String,
        saveCard: Boolean
    ): ResultWrapper<PaymentModel>

    suspend fun createPaymentWithCardId(
        orderId: String,
        apiKey: String,
        cardId: String,
        cvv: String? = null
    ): ResultWrapper<PaymentModel>

    suspend fun isPaymentSuccessful(
        apiKey: String,
        orderToken: String,
        paymentId: String
    ): ResultWrapper<PaymentModel>

}

internal class PaymentRepositoryImpl constructor(
    private val paymentApi: PaymentApi
) : PaymentRepository {

    override suspend fun createCardPayment(
        orderId: String,
        apiKey: String,
        panNumber: String,
        expiryDate: String,
        cvv: String,
        saveCard: Boolean
    ): ResultWrapper<PaymentModel> {
        return safeApiCall(Dispatchers.IO) {
            val paymentResult = paymentApi.createPayment(
                orderId,
                apiKey,
                PaymentRequestDto(pan = panNumber, exp = expiryDate, cvc = cvv, saveCard = saveCard)
            )

            when (paymentResult.status) {
                PaymentModel.STATUS_APPROVED -> PaymentModel.Success
                PaymentModel.STATUS_CAPTURED -> PaymentModel.Success
                PaymentModel.REQUIRES_ACTION -> PaymentModel.Pending(
                    paymentResult.id,
                    paymentResult.action.url
                )
                else -> PaymentModel.Declined(paymentResult.error.code, paymentResult.error.message)
            }
        }
    }

    override suspend fun createPaymentWithCardId(
        orderId: String,
        apiKey: String,
        cardId: String,
        cvv: String?
    ): ResultWrapper<PaymentModel> {
        return safeApiCall(Dispatchers.IO) {
            val paymentResult = paymentApi.createPayment(
                orderId,
                apiKey,
                PaymentRequestDto(cardId = cardId, cvc = cvv)
            )

            when (paymentResult.status) {
                PaymentModel.STATUS_APPROVED -> PaymentModel.Success
                PaymentModel.STATUS_CAPTURED -> PaymentModel.Success
                PaymentModel.REQUIRES_ACTION -> PaymentModel.Pending(
                    paymentResult.id,
                    paymentResult.action.url
                )
                else -> PaymentModel.Declined(paymentResult.error.code, paymentResult.error.message)
            }
        }
    }

    override suspend fun isPaymentSuccessful(
        apiKey: String,
        orderToken: String,
        paymentId: String
    ): ResultWrapper<PaymentModel> {
        return safeApiCall(Dispatchers.IO) {
            val payment = paymentApi.getPaymentById(
                orderToken.getOrderId(),
                paymentId,
                orderToken,
                apiKey
            )

            when (payment.status) {
                PaymentModel.STATUS_APPROVED -> PaymentModel.Success
                PaymentModel.STATUS_CAPTURED -> PaymentModel.Success
                else -> PaymentModel.Declined(payment.error.code, payment.error.message)
            }
        }
    }

}
