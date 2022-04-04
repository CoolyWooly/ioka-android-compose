package kz.ioka.android.ioka.presentation.flows.saveCard

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.R

@Parcelize
internal data class SaveCardLauncher(
    val customerToken: String,
    val configuration: Configuration = Configuration()
) : Parcelable

@Parcelize
data class Configuration(
    val toolbarTitle: String? = null,
    val fieldCornerRadius: Int = 12,
    val saveButtonCornerRadius: Int = 12,
    val saveButtonTextRes: String? = null,
    @ColorRes val saveButtonBackgroundColorRes: Int = R.color.ioka_color_primary,
    @FontRes val fontRes: Int = DEFAULT_FONT
) : Parcelable {

    companion object {
        const val DEFAULT_FONT = -1
    }

}