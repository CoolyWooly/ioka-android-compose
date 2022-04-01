package kz.ioka.android.ioka.presentation.flows.payWithCard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
internal data class PayWithCardLauncher(
    val customerToken: String,
    val orderToken: String,
    val price: BigDecimal,
    val withGooglePay: Boolean
) : Parcelable