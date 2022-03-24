package kz.ioka.android.ioka.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal data class ErrorDto(
    @Expose @SerializedName("code") val code: String,
    @Expose @SerializedName("message") val message: String,
)

internal data class ActionDto(
    @Expose @SerializedName("url") val url: String,
)