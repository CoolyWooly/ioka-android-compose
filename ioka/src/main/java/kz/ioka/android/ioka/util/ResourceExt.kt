package kz.ioka.android.ioka.util

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import kz.ioka.android.ioka.R

internal fun Context.getDrawableFromRes(@DrawableRes drawableRes: Int): Drawable? {
    return ContextCompat.getDrawable(this, drawableRes)
}

internal fun Context.getPrimaryColor(): Int {
    val typedValue = TypedValue()

    val a: TypedArray =
        obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimary))
    val color = a.getColor(0, 0)

    a.recycle()

    return color
}