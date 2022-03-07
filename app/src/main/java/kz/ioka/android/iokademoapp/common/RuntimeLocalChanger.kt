package kz.ioka.android.iokademoapp.common

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Locale

object RuntimeLocaleChanger {

    fun wrapContext(context: Context): Context {
        val savedLocale = createLocaleFromSavedLanguage(context)
        Locale.setDefault(savedLocale)

        val newConfig = Configuration()
        newConfig.setLocale(savedLocale)

        return context.createConfigurationContext(newConfig)
    }

    fun overrideLocale(context: Context) {
        val savedLocale = createLocaleFromSavedLanguage(context)

        Locale.setDefault(savedLocale)

        val newConfig = Configuration()
        newConfig.setLocale(savedLocale)

        context.resources.updateConfiguration(newConfig, context.resources.displayMetrics)

        if (context != context.applicationContext) {
            context.applicationContext.resources.run {
                updateConfiguration(newConfig, displayMetrics)
            }
        }
    }

    private fun createLocaleFromSavedLanguage(
        context: Context
    ): Locale {
        return runBlocking {
            context.dataStore.data.map {
                Locale(it[stringPreferencesKey(CURRENT_LANGUAGE)] ?: "ru")
            }.first()
        }
    }
}