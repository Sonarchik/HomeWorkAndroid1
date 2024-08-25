package com.example.hwone

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

// Extension property для DataStore
private val Context.dataStore by preferencesDataStore("user_prefs")
class UserPreferencesDataStore(private val context: Context) : UserPreferencesInterface {

    private val dataStore = context.dataStore

    companion object {
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    override suspend fun saveLoginData(email: String) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return dataStore.data
            .map { preferences -> preferences[IS_LOGGED_IN_KEY] ?: false }
            .firstOrNull() ?: false
    }

    override suspend fun getEmail(): String? {
        return dataStore.data
            .map { preferences -> preferences[EMAIL_KEY] }
            .firstOrNull()
    }

    override suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
