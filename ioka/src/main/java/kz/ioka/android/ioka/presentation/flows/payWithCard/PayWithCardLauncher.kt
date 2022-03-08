package kz.ioka.android.ioka.presentation.flows.payWithCard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayWithCardLauncher(
    val apiKey: String,
    val customerToken: String,
    val orderToken: String,
) : Parcelable