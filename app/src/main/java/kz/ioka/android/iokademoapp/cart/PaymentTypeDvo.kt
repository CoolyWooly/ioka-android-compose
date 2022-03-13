package kz.ioka.android.iokademoapp.cart

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

sealed class PaymentTypeDvo : Parcelable {

    @Parcelize
    object GooglePayDvo : PaymentTypeDvo()

    @Parcelize
    object PayWithCardDvo : PaymentTypeDvo()

    @Parcelize
    object PayWithCashDvo : PaymentTypeDvo()

    @Parcelize
    class PayWithSavedCardDvo(
        val cardId: String,
        val maskedCardNumber: String,
        @DrawableRes val cardTypeRes: Int,
    ) : PaymentTypeDvo()

}