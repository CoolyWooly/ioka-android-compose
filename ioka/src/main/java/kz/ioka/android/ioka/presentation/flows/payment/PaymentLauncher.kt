package kz.ioka.android.ioka.presentation.flows.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.api.Configuration

@Parcelize
data class PaymentLauncher(
    val orderToken: String,
    val configuration: Configuration? = null
) : Parcelable