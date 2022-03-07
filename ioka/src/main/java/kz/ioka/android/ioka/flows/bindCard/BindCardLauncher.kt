package kz.ioka.android.ioka.flows.bindCard

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.R

@Parcelize
data class BindCardLauncher(
    val apiKey: String,
    val customerToken: String,
    val configuration: Configuration = Configuration()
) : Parcelable

@Parcelize
data class Configuration(
    @StringRes val toolbarTitleRes: Int = R.string.new_card,
    val fieldCornerRadius: Int = 12,
    val saveButtonCornerRadius: Int = 12,
    @StringRes val saveButtonTextRes: Int = R.string.common_save,
    @ColorRes val saveButtonBackgroundColorRes: Int = R.color.ioka_color_primary,
    @FontRes val fontRes: Int = DEFAULT_FONT
) : Parcelable {

    companion object {
        const val DEFAULT_FONT = -1
    }

}