package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withCvv

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.api.CardDvo
import kz.ioka.android.ioka.api.Configuration

@Parcelize
data class CvvPaymentLauncher(
    val orderToken: String,
    val cardDvo: CardDvo,
    val configuration: Configuration? = null
) : Parcelable