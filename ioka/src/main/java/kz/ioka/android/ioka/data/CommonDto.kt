package kz.ioka.android.ioka.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ErrorDto(
    @Expose @SerializedName("code") val code: String,
    @Expose @SerializedName("message") val message: String,
)

data class ActionDto(
    @Expose @SerializedName("url") val url: String,
)