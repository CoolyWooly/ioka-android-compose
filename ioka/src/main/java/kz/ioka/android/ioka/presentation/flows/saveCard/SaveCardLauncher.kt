package kz.ioka.android.ioka.presentation.flows.saveCard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.api.Configuration

@Parcelize
internal data class SaveCardLauncher(
    val customerToken: String,
    val configuration: Configuration? = null
) : Parcelable