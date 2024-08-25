package com.example.hwone.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property для DataStore
private val Context.dataStore by preferencesDataStore("user_prefs")
class UserPreferencesDataStore(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    // Функція для збереження даних логіну
    suspend fun saveLoginData(email: String) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    // Функція для отримання стану логіну
    val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }

    // Функція для отримання збереженої електронної пошти
    val email: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[EMAIL_KEY]
        }

    // Функція для виходу з системи (очищення даних)
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
