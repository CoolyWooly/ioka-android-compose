package kz.ioka.android.ioka.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.lang.Exception

@Parcelize
data class CardModel(
    val id: String?,
    val customerId: String?,
    val createdAt: String?,
    val panMasked: String?,
    val expiryDate: String?,
    val holder: String?,
    val paymentSystem: String?,
    val emitter: String?,
    val cvcRequired: Boolean?,
) : Parcelable

data class CardDvo(
    val cardId: String,
    val cardNumber: String,
    val cardType: String,
    val cvvRequired: Boolean,
)