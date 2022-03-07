package kz.ioka.android.iokademoapp

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kz.ioka.android.iokademoapp.common.RuntimeLocaleChanger

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(RuntimeLocaleChanger.wrapContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        lifecycleScope.launch {
            RuntimeLocaleChanger.overrideLocale(this@BaseActivity)
        }
    }

}