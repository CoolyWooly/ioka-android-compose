package kz.ioka.android.ioka.presentation.flows.payWithSavedCard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.presentation.flows.common.OrderDvo

@Parcelize
internal data class CvvLauncher(
    val orderToken: String,
    val order: OrderDvo,
    val cardId: String,
    val cardNumber: String,
    val cardType: String
) : Parcelable