package kz.ioka.android.iokademoapp.presentation.cart

import android.os.Parcelable
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
        val cardType: CardType,
        val cvvRequired: Boolean
    ) : PaymentTypeDvo()

}