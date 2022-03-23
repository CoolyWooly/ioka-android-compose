package kz.ioka.android.ioka.api.dataSource

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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