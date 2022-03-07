package kz.ioka.android.iokademoapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kz.ioka.android.iokademoapp.cart.CartFragment
import kz.ioka.android.iokademoapp.profile.ProfileFragment

class MainTabAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) CartFragment()
        else ProfileFragment()
    }
}