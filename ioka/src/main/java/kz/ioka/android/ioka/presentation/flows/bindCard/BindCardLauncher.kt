package kz.ioka.android.ioka.presentation.flows.bindCard

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.R

@Parcelize
internal data class BindCardLauncher(
    val customerToken: String,
    val configuration: Configuration = Configuration()
) : Parcelable

@Parcelize
data class Configuration(
    val toolbarTitle: String? = null,
    val fieldCornerRadius: Int = 12,
    val bindButtonCornerRadius: Int = 12,
    val bindButtonTextRes: String? = null,
    @ColorRes val bindButtonBackgroundColorRes: Int = R.color.ioka_color_primary,
    @FontRes val fontRes: Int = DEFAULT_FONT
) : Parcelable {

    companion object {
        const val DEFAULT_FONT = -1
    }

}