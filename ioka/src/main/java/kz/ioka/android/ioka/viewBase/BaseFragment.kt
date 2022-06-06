package kz.ioka.android.ioka.viewBase

import android.os.Parcelable
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

abstract class BaseFragment(layoutResId: Int) : Fragment(layoutResId) {

    companion object {
        const val FRAGMENT_LAUNCHER = "FRAGMENT_LAUNCHER"
    }

    fun <T : Parcelable> launcher(): T? {
        return arguments?.getParcelable(FRAGMENT_LAUNCHER)
    }

    fun addFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            add((requireView().parent as ViewGroup).id, fragment)
            setReorderingAllowed(true)
        }
    }

}