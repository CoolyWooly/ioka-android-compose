package kz.ioka.android.iokademoapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kz.ioka.android.iokademoapp.common.DARK_MODE_ENABLED
import kz.ioka.android.iokademoapp.common.dataStore
import kz.ioka.android.iokademoapp.presentation.profile.language.SelectLanguageActivity.Companion.LANGUAGE_SELECTED_REQUEST_CODE

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var vPager: ViewPager2
    private lateinit var vBottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDarkMode()

        vPager = findViewById(R.id.viewPager)
        vBottomNavigation = findViewById(R.id.bnvTabBar)

        setupViewPager()
    }

    private fun setupDarkMode() {
        lifecycleScope.launch {
            dataStore.data.map {
                it[booleanPreferencesKey(DARK_MODE_ENABLED)]
            }.first { isDarkModeEnabled ->
                if (isDarkModeEnabled == true) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                true
            }
        }
    }

    private fun setupViewPager() {
        vPager.adapter = MainTabAdapter(this)
        vPager.isUserInputEnabled = false

        vBottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_cart -> {
                    vPager.currentItem = 0
                }
                else -> {
                    vPager.currentItem = 1
                }
            }

            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LANGUAGE_SELECTED_REQUEST_CODE && resultCode == RESULT_OK) {
            recreate()
        }
    }

}