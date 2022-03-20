package kz.ioka.android.iokademoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kz.ioka.android.ioka.api.Ioka

@HiltAndroidApp
class iokaDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Ioka.init(BuildConfig.API_KEY)
    }

}