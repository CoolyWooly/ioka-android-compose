package kz.ioka.android.ioka.data.payment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PaymentRequestDto(
    @Expose @SerializedName("pan") val pan: String,
    @Expose @SerializedName("exp") val exp: String,
    @Expose @SerializedName("cvc") val cvc: String,
    @Expose @SerializedName("save") val bindCard: Boolean,
)

data class PaymentResponseDto(
    @Expose @SerializedName("id") val id: String,
    @Expose @SerializedName("order_id") val orderId: String,
    @Expose @SerializedName("status") val status: String,
    @Expose @SerializedName("created_at") val createdAt: String,
    @Expose @SerializedName("error") val error: ErrorDto,
    @Expose @SerializedName("action") val action: ActionDto,
)

data class ErrorDto(
    @Expose @SerializedName("code") val code: String,
    @Expose @SerializedName("message") val message: String,
)

data class ActionDto(
    @Expose @SerializedName("url") val url: String,
)