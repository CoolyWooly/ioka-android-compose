package kz.ioka.android.ioka.domain.payment

import kotlinx.coroutines.Dispatchers
import kz.ioka.android.ioka.data.payment.PaymentApi
import kz.ioka.android.ioka.data.payment.PaymentRequestDto
import kz.ioka.android.ioka.domain.common.ResultWrapper
import kz.ioka.android.ioka.domain.common.safeApiCall
import kz.ioka.android.ioka.util.getOrderId

internal interface PaymentRepository {

    suspend fun createCardPayment(
        orderId: String,
        customerToken: String,
        apiKey: String,
        panNumber: String,
        expiryDate: String,
        cvv: String,
        bindCard: Boolean
    ): ResultWrapper<PaymentModel>

    suspend fun createPaymentWithCardId(
        orderId: String,
        customerToken: String,
        apiKey: String,
        cardId: String,
        cvv: String
    ): ResultWrapper<PaymentModel>

    suspend fun isPaymentSuccessful(
        apiKey: String,
        customerToken: String,
        orderToken: String,
        paymentId: String
    ): ResultWrapper<Boolean>

}

internal class PaymentRepositoryImpl constructor(
    private val paymentApi: PaymentApi
) : PaymentRepository {

    override suspend fun createCardPayment(
        orderId: String,
        customerToken: String,
        apiKey: String,
        panNumber: String,
        expiryDate: String,
        cvv: String,
        bindCard: Boolean
    ): ResultWrapper<PaymentModel> {
        return safeApiCall(Dispatchers.IO) {
            val paymentResult = paymentApi.createPayment(
                orderId,
                customerToken,
                apiKey,
                PaymentRequestDto(pan = panNumber, exp = expiryDate, cvc = cvv, bindCard = bindCard)
            )

            when (paymentResult.status) {
                PaymentModel.STATUS_APPROVED -> PaymentModel.Success
                PaymentModel.STATUS_CAPTURED -> PaymentModel.Success
                PaymentModel.REQUIRES_ACTION -> PaymentModel.Pending(
                    paymentResult.id,
                    paymentResult.action.url
                )
                else -> PaymentModel.Declined
            }
        }
    }

    override suspend fun createPaymentWithCardId(
        orderId: String,
        customerToken: String,
        apiKey: String,
        cardId: String,
        cvv: String
    ): ResultWrapper<PaymentModel> {
        return safeApiCall(Dispatchers.IO) {
            val paymentResult = paymentApi.createPayment(
                orderId,
                customerToken,
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
                else -> PaymentModel.Declined
            }
        }
    }

    override suspend fun isPaymentSuccessful(
        apiKey: String,
        customerToken: String,
        orderToken: String,
        paymentId: String
    ): ResultWrapper<Boolean> {
        return safeApiCall(Dispatchers.IO) {
            val payment = paymentApi.getPaymentById(
                orderToken.getOrderId(),
                paymentId,
                customerToken,
                orderToken,
                apiKey
            )

            payment.status == PaymentModel.STATUS_APPROVED || payment.status == PaymentModel.STATUS_CAPTURED
        }
    }

}
