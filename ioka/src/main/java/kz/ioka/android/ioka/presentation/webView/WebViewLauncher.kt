package kz.ioka.android.ioka.presentation.webView

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class WebViewLauncher(
    val toolbarTitle: String,
    val actionUrl: String
) : Parcelable