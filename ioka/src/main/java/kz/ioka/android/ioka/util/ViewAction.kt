package kz.ioka.android.ioka.util

import android.app.Activity
import androidx.fragment.app.FragmentActivity

internal open class ViewAction(
    open var singleAction: (FragmentActivity) -> Unit
) {

    open fun invoke(activity: FragmentActivity) {
        singleAction.invoke(activity)
    }

}