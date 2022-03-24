package kz.ioka.android.ioka.data.cardInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal data class BrandResponseDto(
    @Expose @SerializedName("brand") val brand: String
)

internal data class EmitterResponseDto(
    @Expose @SerializedName("code") val code: String,
    @Expose @SerializedName("brand") val brand: String,
    @Expose @SerializedName("type") val type: String,
    @Expose @SerializedName("emitter_code") val emitterCode: String,
    @Expose @SerializedName("emitter_name") val emitterName: String

)