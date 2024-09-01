package com.example.hwone.userdto

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hwone.Constants.EXTRA_EMAIL
import com.example.hwone.Constants.EXTRA_PASSWORD
import com.example.hwone.Constants.EXTRA_REMEMBER
import com.example.hwone.Constants.IS_LOGGED_IN
import com.example.hwone.Constants.USER_PREFS
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

// Extension property for DataStore
private val Context.dataStore by preferencesDataStore(USER_PREFS)

class UserPreferencesDataStore(private val context: Context) : UserPreferencesInterface {

    private val dataStore = context.dataStore

    // Private function to retrieve a value from DataStore
    private suspend fun <T> getValue(key: Preferences.Key<T>, defaultValue: T? = null): T? {
        return dataStore.data
            .map { preferences -> preferences[key] ?: defaultValue }
            .firstOrNull()
    }

    // Private function to save a value in DataStore
    private suspend fun <T> saveValue(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun saveLoginData(email: String, password: String, rememberMe: Boolean) {
        saveValue(EMAIL_KEY, email)
        saveValue(PASSWORD_KEY, if (rememberMe) password else "")
        saveValue(REMEMBER_ME_KEY, rememberMe)
        saveValue(IS_LOGGED_IN_KEY, true)
    }

    override suspend fun isLoggedIn(): Boolean {
        return getValue(IS_LOGGED_IN_KEY, false) ?: false
    }

    override suspend fun isRememberMeChecked(): Boolean {
        return getValue(REMEMBER_ME_KEY, false) ?: false
    }

    override suspend fun getEmail(): String? {
        return getValue(EMAIL_KEY)
    }

    override suspend fun getPassword(): String? {
        return getValue(PASSWORD_KEY)
    }

    override suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val EMAIL_KEY = stringPreferencesKey(EXTRA_EMAIL)
        private val PASSWORD_KEY = stringPreferencesKey(EXTRA_PASSWORD)
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey(IS_LOGGED_IN)
        private val REMEMBER_ME_KEY = booleanPreferencesKey(EXTRA_REMEMBER)
    }
}
