package kz.ioka.android.ioka.data.payment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kz.ioka.android.ioka.data.ActionDto
import kz.ioka.android.ioka.data.ErrorDto

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