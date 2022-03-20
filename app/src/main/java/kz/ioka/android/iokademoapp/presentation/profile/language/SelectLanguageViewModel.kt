package kz.ioka.android.iokademoapp.presentation.profile.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kz.ioka.android.iokademoapp.common.Locale
import kz.ioka.android.iokademoapp.common.LocaleUtils
import kz.ioka.android.iokademoapp.data.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class SelectLanguageViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {

    private val _selectedLanguage = MutableLiveData(Locale.RU)
    val selectedLanguage = _selectedLanguage as LiveData<Locale>

    init {
        runBlocking {
            settingsRepository.observeCurrentLanguage().first {
                _selectedLanguage.value = LocaleUtils.getLocaleByValue(it)

                true
            }
        }
    }

    fun onLanguageSelected(locale: Locale) {
        _selectedLanguage.value = locale
    }

    fun onSaveClicked(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.setCurrentLanguage(selectedLanguage.value!!.value)
        }

        block.invoke()
    }

}