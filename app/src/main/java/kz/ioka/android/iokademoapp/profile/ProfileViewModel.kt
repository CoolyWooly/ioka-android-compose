package kz.ioka.android.iokademoapp.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kz.ioka.android.iokademoapp.common.Locale
import kz.ioka.android.iokademoapp.common.LocaleUtils
import kz.ioka.android.iokademoapp.data.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {

    private val _selectedLanguage = MutableLiveData(Locale.RU)
    val selectedLanguage = _selectedLanguage as LiveData<Locale>

    private val _isDarkModeEnabled = MutableLiveData(false)
    val isDarkModeEnabled = _isDarkModeEnabled as LiveData<Boolean>

    private val _progress = MutableLiveData(false)
    val progress = _progress as LiveData<Boolean>

    init {
        initLanguage()
        initDarkModeValue()
    }

    private fun initLanguage() {
        viewModelScope.launch {
            settingsRepository.observeCurrentLanguage().collect {
                _selectedLanguage.postValue(LocaleUtils.getLocaleByValue(it))
            }
        }
    }

    private fun initDarkModeValue() {
        viewModelScope.launch {
            settingsRepository.observeIsDarkModeEnabled().collect {
                _isDarkModeEnabled.value = it
            }
        }
    }

    fun onDarkModeSwitchChanged(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkModeEnabled(isEnabled)
        }

        _isDarkModeEnabled.value = isEnabled
    }

    fun onSavedCardsClicked(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _progress.postValue(true)

            settingsRepository.getProfile()
            _progress.postValue(false)

            block.invoke()
        }
    }

}