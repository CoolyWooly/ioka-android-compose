package kz.ioka.android.ioka.presentation.flows.saveCard

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.R

@Parcelize
internal data class SaveCardLauncher(
    val customerToken: String,
    val configuration: Configuration? = null
) : Parcelable

@Parcelize
data class Configuration(
    val buttonText: String? = null,
    @DrawableRes val fieldBackground: Int? = null,
    @DrawableRes val buttonBackground: Int? = null,
    @ColorRes val backgroundColor: Int = R.color.ioka_color_background_primary,
    @ColorRes val iconColor: Int = R.color.ioka_color_icon_secondary,
    @ColorRes val textColor: Int = R.color.ioka_color_text_primary,
    @ColorRes val hintColor: Int = R.color.ioka_color_text_secondary,
    @FontRes val fontRes: Int = DEFAULT_FONT
) : Parcelable {

    companion object {
        const val DEFAULT_FONT = -1
    }

}