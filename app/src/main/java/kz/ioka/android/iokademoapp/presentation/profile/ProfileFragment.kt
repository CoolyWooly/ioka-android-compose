package kz.ioka.android.iokademoapp.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint
import kz.ioka.android.R
import kz.ioka.android.iokademoapp.common.Locale
import kz.ioka.android.iokademoapp.presentation.profile.language.SelectLanguageActivity
import kz.ioka.android.iokademoapp.presentation.profile.savedCards.SavedCardsActivity

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var btnSavedCards: LinearLayoutCompat
    private lateinit var btnLanguages: LinearLayoutCompat
    private lateinit var tvCurrentLanguage: TextView
    private lateinit var vDarkModeContainer: LinearLayoutCompat
    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var vProgress: FrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSavedCards = view.findViewById(R.id.btnSavedCards)
        btnLanguages = view.findViewById(R.id.btnLanguages)
        tvCurrentLanguage = view.findViewById(R.id.tvCurrentLanguage)
        vDarkModeContainer = view.findViewById(R.id.vDarkModeContainer)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        vProgress = view.findViewById(R.id.vProgress)

        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        viewModel.apply {
            selectedLanguage.observe(viewLifecycleOwner) {
                val languageToDisplay = when (it) {
                    Locale.RU -> getString(R.string.language_ru)
                    Locale.EN -> getString(R.string.language_en)
                    else -> getString(R.string.language_kz)
                }

                tvCurrentLanguage.text = languageToDisplay
            }

            isDarkModeEnabled.observe(viewLifecycleOwner) {
                switchDarkMode.isChecked = it
            }

            progress.observe(viewLifecycleOwner) {
                vProgress.isVisible = it
            }
        }
    }

    private fun setupListeners() {
        btnSavedCards.setOnClickListener {
            val intent = Intent(requireActivity(), SavedCardsActivity::class.java)
            startActivity(intent)
        }

        btnLanguages.setOnClickListener {
            val intent = Intent(requireContext(), SelectLanguageActivity::class.java)
            requireActivity().startActivityForResult(
                intent,
                SelectLanguageActivity.LANGUAGE_SELECTED_REQUEST_CODE
            )
        }

        vDarkModeContainer.setOnClickListener {
            viewModel.onDarkModeSwitchChanged(!switchDarkMode.isChecked)
            applyDarkModeChange(!switchDarkMode.isChecked)
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onDarkModeSwitchChanged(isChecked)
            applyDarkModeChange(isChecked)
        }
    }

    private fun applyDarkModeChange(isEnabled: Boolean) {
        if (isEnabled) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
    }

}