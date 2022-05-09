package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withoutCvv

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PayWithCardIdLauncher(
    val orderToken: String,
    val cardId: String
) : Parcelable