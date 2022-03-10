package kz.ioka.android.ioka.data.payment

import retrofit2.http.*

interface PaymentApi {

    @POST("/v2/orders/{order_id}/payments/card")
    suspend fun createPayment(
        @Path("order_id") orderId: String,
        @Header("X-Customer-Access-Token") customerToken: String,
        @Header("API-KEY") apiKey: String,
        @Body requestDto: PaymentRequestDto
    ): PaymentResponseDto

    @GET("/v2/orders/{order_id}/payments/{payment_id}")
    suspend fun getPaymentById(
        @Path("order_id") orderId: String,
        @Path("payment_id") paymentId: String,
        @Header("X-Customer-Access-Token") customerToken: String,
        @Header("X-Order-Access-Token") orderToken: String,
        @Header("API-KEY") apiKey: String
    ): PaymentResponseDto

}