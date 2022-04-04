package kz.ioka.android.ioka.viewBase

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity

internal abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val LAUNCHER = "BaseActivity_LAUNCHER"
        const val SCAN_REQUEST_CODE = 420
    }

    fun <T : Parcelable> launcher(): T? {
        return intent.getParcelableExtra(LAUNCHER)
    }

}