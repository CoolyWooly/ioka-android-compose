package kz.ioka.android.ioka.data.cardInfo

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface CardInfoApi {

    @GET("/v2/brands")
    suspend fun getBrand(
        @Query("partial_bin") partialBin: String
    ): Response<BrandResponseDto>

    @GET("/v2/bins/{bin_code}")
    suspend fun getEmitter(
        @Path("bin_code") binCode: String
    ): Response<EmitterResponseDto>

}