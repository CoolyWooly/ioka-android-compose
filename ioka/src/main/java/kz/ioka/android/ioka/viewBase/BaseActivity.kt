package kz.ioka.android.ioka.viewBase

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val LAUNCHER = "BaseActivity_LAUNCHER"
    }

    fun <T : Parcelable> launcher(): T? {
        return intent.getParcelableExtra(LAUNCHER)
    }

}