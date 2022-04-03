package kz.ioka.android.ioka.presentation.flows.payWithBindedCard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.presentation.flows.common.OrderDvo

@Parcelize
internal data class CvvLauncher(
    val customerToken: String,
    val orderToken: String,
    val order: OrderDvo,
    val cardId: String,
    val cardNumber: String,
    val cardType: String
) : Parcelable