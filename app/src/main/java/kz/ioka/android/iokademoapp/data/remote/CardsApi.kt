package kz.ioka.android.iokademoapp.data.remote

import retrofit2.http.*

interface CardsApi {

    @GET("/v2/customers/{customer_id}/cards")
    suspend fun getCards(
        @Path("customer_id") customerId: String,
        @Header("API-KEY") apiKey: String,
        @Header("X-Customer-Access-Token") customerToken: String
    ): List<CardsResultDto>

    @DELETE("/v2/customers/{customer_id}/cards/{card_id}")
    suspend fun removeCard(
        @Path("customer_id") customerId: String,
        @Path("card_id") cardId: String,
        @Header("API-KEY") apiKey: String,
        @Header("X-Customer-Access-Token") customerToken: String
    )

}