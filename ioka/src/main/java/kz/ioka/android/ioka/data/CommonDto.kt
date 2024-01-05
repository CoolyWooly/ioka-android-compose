package kz.ioka.android.ioka.data

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
internal data class ErrorDto(
    @Expose @SerializedName("code") val code: String,
    @Expose @SerializedName("message") val message: String,
)

@Keep
internal data class ActionDto(
    @Expose @SerializedName("url") val url: String,
)