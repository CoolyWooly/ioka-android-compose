package kz.ioka.android.iokademoapp.data.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ProfileResponseDto(
    @Expose @SerializedName("customer_access_token") val customerToken: String?
)

data class CheckoutRequestDto(
    @Expose @SerializedName("price") val price: BigDecimal
)

data class CheckoutResponseDto(
    @Expose @SerializedName("order_access_token") val orderToken: String?,
    @Expose @SerializedName("customer_access_token") val customerToken: String?
)