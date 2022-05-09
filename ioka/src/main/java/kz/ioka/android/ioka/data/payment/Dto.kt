package kz.ioka.android.ioka.data.payment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kz.ioka.android.ioka.data.ActionDto
import kz.ioka.android.ioka.data.ErrorDto

internal data class PaymentRequestDto(
    @Expose @SerializedName("pan") val pan: String? = null,
    @Expose @SerializedName("exp") val exp: String? = null,
    @Expose @SerializedName("cvc") val cvc: String? = null,
    @Expose @SerializedName("save") val saveCard: Boolean? = null,
    @Expose @SerializedName("card_id") val cardId: String? = null,
)

internal data class PaymentResponseDto(
    @Expose @SerializedName("id") val id: String,
    @Expose @SerializedName("order_id") val orderId: String,
    @Expose @SerializedName("status") val status: String,
    @Expose @SerializedName("created_at") val createdAt: String,
    @Expose @SerializedName("error") val error: ErrorDto,
    @Expose @SerializedName("action") val action: ActionDto,
)