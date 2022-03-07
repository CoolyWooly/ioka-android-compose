package kz.ioka.android.ioka.viewBase

import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val LAUNCHER = "BaseActivity_LAUNCHER"
    }

    fun <T : Parcelable> launcher(): T? {
        return intent.getParcelableExtra(LAUNCHER)
    }

    fun getDrawableFromRes(@DrawableRes drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(this, drawableRes)
    }

}