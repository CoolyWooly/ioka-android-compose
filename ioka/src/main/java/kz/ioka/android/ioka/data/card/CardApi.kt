package kz.ioka.android.ioka.data.card

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CardApi {

    @POST("/v2/customers/{customer_id}/bindings")
    suspend fun bindCard(
        @Path("customer_id") customerId: String,
        @Header("X-Customer-Access-Token") customerToken: String,
        @Header("API-KEY") apiKey: String,
        @Body requestDto: BindCardRequestDto
    ): BindCardResponseDto

}