package kz.ioka.android.ioka.flows.payWithCard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayWithCardLauncher(
    val apiKey: String,
    val customerToken: String,
    val orderToken: String,
) : Parcelable