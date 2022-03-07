package kz.ioka.android.iokademoapp.data.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ErrorDto(
    @Expose @SerializedName("code") val code: String?,
    @Expose @SerializedName("message") val message: String?
)

data class ActionDto(
    @Expose @SerializedName("url") val url: String?,
)

data class CardsResultDto(
    @Expose @SerializedName("id") val id: String?,
    @Expose @SerializedName("customer_id") val customer_id: String?,
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