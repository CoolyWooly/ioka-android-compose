package kz.ioka.android.ioka.util

import android.content.res.Resources
import android.util.TypedValue
import kz.ioka.android.ioka.domain.common.Amount
import kz.ioka.android.ioka.domain.common.Currency
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat

internal val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

internal fun BigDecimal.toAmountFormat(): String {
    val formatter: NumberFormat = DecimalFormat("#,###.##")
    val myNumber = this
    return "${formatter.format(myNumber)} ₸"
}

internal fun Int.toAmount(currency: Currency = Currency.KZT): Amount {
    val amount = toDouble() / 100

    return Amount(
        BigDecimal(amount),
        currency,
    )
}