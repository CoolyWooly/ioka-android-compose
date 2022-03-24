package kz.ioka.android.ioka.presentation.flows.payWithBindedCard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CvvLauncher(
    val customerToken: String,
    val orderToken: String,
    val price: Int,
    val cardId: String,
    val cardNumber: String,
    val cardType: String,
    val cvvRequired: Boolean
) : Parcelable