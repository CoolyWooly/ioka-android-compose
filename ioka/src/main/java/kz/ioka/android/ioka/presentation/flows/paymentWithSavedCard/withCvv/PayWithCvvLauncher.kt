package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withCvv

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.api.Configuration
import kz.ioka.android.ioka.presentation.flows.common.OrderDvo

@Parcelize
internal data class PayWithCvvLauncher(
    val orderToken: String,
    val order: OrderDvo,
    val cardId: String,
    val cardNumber: String,
    val cardType: String,
    val configuration: Configuration? = null
) : Parcelable