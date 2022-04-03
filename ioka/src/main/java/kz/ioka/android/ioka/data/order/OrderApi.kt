package kz.ioka.android.ioka.data.order

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface OrderApi {

    @GET("/v2/orders/{order_id}")
    suspend fun getOrderById(
        @Path("order_id") orderId: String
    ): OrderResponseDto

}