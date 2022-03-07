package kz.ioka.android.iokademoapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kz.ioka.android.iokademoapp.common.*
import kz.ioka.android.iokademoapp.data.local.ProfileDao
import kz.ioka.android.iokademoapp.data.remote.DemoApi
import javax.inject.Inject

interface SettingsRepository {

    fun observeCurrentLanguage(): Flow<String>
    suspend fun setCurrentLanguage(language: String)
    fun observeIsDarkModeEnabled(): Flow<Boolean>
    suspend fun setDarkModeEnabled(enabled: Boolean)

    suspend fun getProfile()

}

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val demoApi: DemoApi,
    private val profileDao: ProfileDao
) : SettingsRepository {

    private val dataStore = context.dataStore

    override fun observeCurrentLanguage(): Flow<String> {
        return dataStore.data.map {
            it[stringPreferencesKey(CURRENT_LANGUAGE)] ?: Locale.RU.value
        }
    }

    override suspend fun setCurrentLanguage(language: String) {
        dataStore.edit {
            it[stringPreferencesKey(CURRENT_LANGUAGE)] = language
        }
    }

    override fun observeIsDarkModeEnabled(): Flow<Boolean> {
        return dataStore.data.map {
            it[booleanPreferencesKey(DARK_MODE_ENABLED)] ?: false
        }
    }

    override suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit {
            it[booleanPreferencesKey(DARK_MODE_ENABLED)] = enabled
        }
    }

    override suspend fun getProfile() {
        val customerToken = demoApi.getCustomerToken().customerToken

        customerToken?.let {
            profileDao.setCustomerToken(it)
        }
    }

}