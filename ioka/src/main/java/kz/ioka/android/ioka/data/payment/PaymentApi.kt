package kz.ioka.android.ioka.data.payment

import retrofit2.http.*

internal interface PaymentApi {

    companion object {
        const val PREFIX = "/v2/orders"
    }

    @POST("$PREFIX/{order_id}/payments/card")
    suspend fun createPayment(
        @Path("order_id") orderId: String,
        @Header("API-KEY") apiKey: String,
        @Body requestDto: PaymentRequestDto
    ): PaymentResponseDto

    @GET("$PREFIX/{order_id}/payments/{payment_id}")
    suspend fun getPaymentById(
        @Path("order_id") orderId: String,
        @Path("payment_id") paymentId: String,
        @Header("X-Order-Access-Token") orderToken: String,
        @Header("API-KEY") apiKey: String
    ): PaymentResponseDto

}