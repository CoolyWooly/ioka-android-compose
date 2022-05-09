package kz.ioka.android.ioka.data.order

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal data class OrderResponseDto(
    @Expose @SerializedName("id") val id: String? = null,
    @Expose @SerializedName("status") val status: String? = null,
    @Expose @SerializedName("created_at") val created_at: String? = null,
    @Expose @SerializedName("amount") val amount: Int? = null,
    @Expose @SerializedName("currency") val currency: String? = null,
    @Expose @SerializedName("capture_method") val capture_method: String? = null,
    @Expose @SerializedName("external_id") val external_id: String? = null,
    @Expose @SerializedName("description") val description: String? = null,
    @Expose @SerializedName("attempts") val attempts: Int? = null,
    @Expose @SerializedName("due_date") val due_date: String? = null,
    @Expose @SerializedName("customer_id") val customer_id: String? = null,
    @Expose @SerializedName("card_id") val card_id: String? = null,
    @Expose @SerializedName("back_url") val back_url: String? = null,
    @Expose @SerializedName("success_url") val success_url: String? = null,
    @Expose @SerializedName("failure_url") val failure_url: String? = null,
    @Expose @SerializedName("template") val template: String? = null,
    @Expose @SerializedName("checkout_url") val checkout_url: String? = null,
)
