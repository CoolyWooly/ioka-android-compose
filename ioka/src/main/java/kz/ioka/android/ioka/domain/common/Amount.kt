package kz.ioka.android.ioka.domain.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class Amount(
    val amount: BigDecimal,
    val currency: Currency = Currency.KZT
) : Parcelable