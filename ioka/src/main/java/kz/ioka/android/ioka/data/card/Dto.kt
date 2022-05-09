package kz.ioka.android.ioka.data.card

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kz.ioka.android.ioka.data.ActionDto
import kz.ioka.android.ioka.data.ErrorDto

internal data class CardResultDto(
    @Expose @SerializedName("id") val id: String?,
    @Expose @SerializedName("customer_id") val customer_id: String?,
    @Expose @SerializedName("status") val status: String?,
    @Expose @SerializedName("created_at") val created_at: String?,
    @Expose @SerializedName("pan_masked") val pan_masked: String?,
    @Expose @SerializedName("expiry_date") val expiry_date: String?,
    @Expose @SerializedName("holder") val holder: String?,
    @Expose @SerializedName("payment_system") val payment_system: String?,
    @Expose @SerializedName("emitter") val emitter: String?,
    @Expose @SerializedName("cvc_required") val cvc_required: Boolean?,
    @Expose @SerializedName("error") val error: ErrorDto?,
    @Expose @SerializedName("action") val action: ActionDto?
)

internal data class SaveCardRequestDto(
    @Expose @SerializedName("pan") val pan: String,
    @Expose @SerializedName("exp") val exp: String,
    @Expose @SerializedName("cvc") val cvc: String,
    @Expose @SerializedName("holder") val holder: String? = null,
)

internal data class SaveCardResponseDto(
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
    @Expose @SerializedName("error") val error: ErrorDto,
    @Expose @SerializedName("action") val action: ActionDto,
)