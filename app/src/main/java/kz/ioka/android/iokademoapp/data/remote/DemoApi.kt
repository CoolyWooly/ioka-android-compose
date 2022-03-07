package kz.ioka.android.iokademoapp.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DemoApi {

    @GET("/profile?platform=android")
    suspend fun getCustomerToken(): ProfileResponseDto

    @POST("/checkout?platform=android")
    suspend fun checkout(@Body requestDto: CheckoutRequestDto): CheckoutResponseDto

}