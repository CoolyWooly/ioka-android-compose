package kz.ioka.android.ioka.presentation.launcher

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow
import kz.ioka.android.ioka.util.ViewAction

internal interface PaymentLauncherBehavior : Parcelable {

    val titleRes: Int

    fun observeProgress(): Flow<Boolean>

    suspend fun doOnLoading()

    fun doAfterLoading(): ViewAction

}