package kz.ioka.android.ioka.presentation.flows.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.domain.common.Amount

@Parcelize
data class OrderDvo(
    val externalId: String,
    val amount: Amount,
) : Parcelable
