package com.example.hwone.userdto

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hwone.Constants.USER_PREFS
import com.example.hwone.Constants.EXTRA_EMAIL
import com.example.hwone.Constants.EXTRA_PASSWORD
import com.example.hwone.Constants.EXTRA_REMEMBER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property for DataStore
private val Context.dataStore by preferencesDataStore(USER_PREFS)

class UserPreferencesDataStore(private val context: Context) : UserPreferencesInterface {

    private val dataStore get() = context.dataStore

    // Private function to retrieve a value from DataStore as Flow
    private fun <T> getValue(key: Preferences.Key<T>, defaultValue: T? = null): Flow<T?> {
        return dataStore.data
            .map { preferences -> preferences[key] ?: defaultValue }
    }

    // Private function to save a value in DataStore
    private suspend fun <T> saveValue(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun saveLoginData(email: String, password: String, rememberMe: Boolean) {
        saveValue(stringPreferencesKey( EMAIL_KEY), email)
        saveValue(stringPreferencesKey( PASSWORD_KEY), if (rememberMe) password else "")
        saveValue(booleanPreferencesKey( REMEMBER_ME_KEY), rememberMe)
    }

    // Return Flow<Boolean> for "Remember Me" state
    override suspend fun isRememberMeChecked(): Flow<Boolean> {
        return getValue(booleanPreferencesKey( REMEMBER_ME_KEY), false).map { it ?: false }
    }

    // Return Flow<String?> for email
    override suspend fun getEmail(): Flow<String?> {
        return getValue(stringPreferencesKey( EMAIL_KEY))
    }

    // Return Flow<String?> for password
    override suspend fun getPassword(): Flow<String?> {
        return getValue(stringPreferencesKey( PASSWORD_KEY))
    }

    override suspend fun logout() {
        dataStore.edit { preferences ->
            val emailKey  = stringPreferencesKey(EMAIL_KEY)
            val passwordKey = stringPreferencesKey(PASSWORD_KEY)
            val rememberMeKey = stringPreferencesKey(REMEMBER_ME_KEY)

            preferences.remove(emailKey )
            preferences.remove(passwordKey)
            preferences.remove(rememberMeKey)
        }
    }

    companion object {
        private const val EMAIL_KEY = EXTRA_EMAIL
        private const val PASSWORD_KEY = EXTRA_PASSWORD
        private const val REMEMBER_ME_KEY = EXTRA_REMEMBER
    }
}
