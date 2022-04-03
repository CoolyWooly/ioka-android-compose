package kz.ioka.android.ioka.presentation.flows.payWithCard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.presentation.flows.common.OrderDvo

@Parcelize
internal data class PayWithCardLauncher(
    val orderToken: String,
    val order: OrderDvo,
    val withGooglePay: Boolean,
    val canBindCard: Boolean
) : Parcelable