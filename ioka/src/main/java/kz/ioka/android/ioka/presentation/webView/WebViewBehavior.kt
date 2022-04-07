package kz.ioka.android.ioka.presentation.webView

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow

interface WebViewBehavior : Parcelable {

    val toolbarTitleRes: Int

    val actionUrl: String

    fun observeProgress(): Flow<Boolean>

    suspend fun onActionFinished(): ResultState

}