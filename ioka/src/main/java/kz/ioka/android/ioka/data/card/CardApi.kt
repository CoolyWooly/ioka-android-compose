package kz.ioka.android.ioka.data.card

import retrofit2.Response
import retrofit2.http.*

internal interface CardApi {

    @POST("/v2/customers/{customer_id}/bindings")
    suspend fun bindCard(
        @Path("customer_id") customerId: String,
        @Header("X-Customer-Access-Token") customerToken: String,
        @Header("API-KEY") apiKey: String,
        @Body requestDto: BindCardRequestDto
    ): BindCardResponseDto

    @GET("/v2/customers/{customer_id}/cards")
    suspend fun getCards(
        @Header("API-KEY") apiKey: String,
        @Header("X-Customer-Access-Token") customerToken: String,
        @Path("customer_id") customerId: String,
    ): List<CardResultDto>

    @HTTP(method = "DELETE", path = "/v2/customers/{customer_id}/cards/{card_id}", hasBody = true)
    suspend fun removeCard(
        @Header("API-KEY") apiKey: String,
        @Header("X-Customer-Access-Token") customerToken: String,
        @Path("customer_id") customerId: String,
        @Path("card_id") cardId: String,
    ): Response<Void>

}