package kz.ioka.android.ioka.domain.order

import kz.ioka.android.ioka.domain.common.Amount

internal data class OrderModel(
    val externalId: String,
    val customerId: String? = null,
    val amount: Amount,
)