package kz.ioka.android.iokademoapp.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kz.ioka.android.iokademoapp.presentation.cart.CardType

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_PREFERENCES)

fun String?.toCardType(): CardType {
    if (this == null) {
        return CardType.UNKNOWN
    }

    return CardType.values().first { it.code == this }
}