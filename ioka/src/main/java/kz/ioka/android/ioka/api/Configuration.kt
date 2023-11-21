package kz.ioka.android.ioka.api

import android.content.res.Resources.Theme
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.R

@Parcelize
data class Configuration(
    val buttonText: String? = null,
    @DrawableRes val fieldBackground: Int? = null,
    @DrawableRes val buttonBackground: Int? = null,
    @StyleRes val themeId: Int = R.style.IokaTheme_Transparent,
    @ColorRes val backgroundColor: Int = R.color.ioka_color_background,
    @ColorRes val iconColor: Int = R.color.ioka_color_icon_secondary,
    @ColorRes val textColor: Int = R.color.ioka_color_text,
    @ColorRes val hintColor: Int = R.color.ioka_color_nonadaptable_grey,
) : Parcelable