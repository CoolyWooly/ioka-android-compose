package kz.ioka.android.ioka.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import kz.ioka.android.ioka.R

fun FragmentManager.addFragment(fragment: Fragment) = commit {
    addToBackStack(null)
    add(R.id.fcvContainer, fragment)
    setReorderingAllowed(true)
}

fun FragmentManager.replaceFragment(fragment: Fragment) = commit {
    addToBackStack(null)
    replace(R.id.fcvContainer, fragment)
    setReorderingAllowed(true)
}