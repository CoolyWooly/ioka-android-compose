package kz.ioka.android.iokademoapp.common

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_PREFERENCES)

fun String.shortPanMask(): String {
    return "•••• ${takeLast(4)}"
}

val Number.toPx
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