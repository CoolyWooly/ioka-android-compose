package kz.ioka.android.ioka.util

import android.content.res.Resources
import android.util.TypedValue

internal val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

internal val Number.toAmount
    get() = String.format("%s ₸", toString())