package kz.ioka.android.ioka.viewBase

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity

internal abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val LAUNCHER = "BaseActivity_LAUNCHER"

        inline fun <reified T : BaseActivity> provideIntent(
            activity: Activity,
            launcher: Parcelable
        ): Intent {
            return Intent(activity, T::class.java).putExtra(LAUNCHER, launcher)
        }
    }

    fun <T : Parcelable> launcher(): T? {
        return intent.getParcelableExtra(LAUNCHER)
    }

}