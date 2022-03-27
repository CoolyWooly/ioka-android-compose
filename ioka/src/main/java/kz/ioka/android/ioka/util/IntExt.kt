package kz.ioka.android.ioka.util

import android.content.res.Resources
import android.util.TypedValue
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat

internal val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

fun BigDecimal.toAmountFormat(): String {
    val formatter: NumberFormat = DecimalFormat("#,###.##")
    val myNumber = this
    return "${formatter.format(myNumber)} ₸"
}