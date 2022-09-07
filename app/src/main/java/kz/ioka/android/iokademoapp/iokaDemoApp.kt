package kz.ioka.android.iokademoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kz.ioka.android.ioka.api.Ioka
import timber.log.Timber

@HiltAndroidApp
class iokaDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Ioka.init(BuildConfig.TEST_API_KEY)
        Timber.plant(Timber.DebugTree())
    }

}