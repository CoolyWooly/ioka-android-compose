package kz.ioka.android.iokademoapp.presentation.cart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.api.CardBrandModel

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
        val cardType: CardBrandModel,
        val cvvRequired: Boolean
    ) : PaymentTypeDvo()

}