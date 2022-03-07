package kz.ioka.android.ioka.data.card

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BindCardRequestDto(
    @Expose @SerializedName("pan") val pan: String,
    @Expose @SerializedName("exp") val exp: String,
    @Expose @SerializedName("cvc") val cvc: String,
    @Expose @SerializedName("holder") val holder: String? = null,
)

data class BindCardResponseDto(
    @Expose @SerializedName("id") val id: String,
    @Expose @SerializedName("customer_id") val customerId: String,
    @Expose @SerializedName("status") val status: String,
    @Expose @SerializedName("created_at") val createdAt: String,
    @Expose @SerializedName("pan_masked") val panMasked: String,
    @Expose @SerializedName("expiry_date") val expiryDate: String,
    @Expose @SerializedName("holder") val holder: String,
    @Expose @SerializedName("payment_system") val paymentSystem: String,
    @Expose @SerializedName("emitter") val emitter: String,
    @Expose @SerializedName("cvc_required") val cvcRequired: Boolean,
    @Expose @SerializedName("error") val error: BindCardErrorDto,
    @Expose @SerializedName("action") val action: BindCardActionDto,
)

data class BindCardErrorDto(
    @Expose @SerializedName("code") val code: String,
    @Expose @SerializedName("message") val message: String,
)

data class BindCardActionDto(
    @Expose @SerializedName("url") val url: String,
)