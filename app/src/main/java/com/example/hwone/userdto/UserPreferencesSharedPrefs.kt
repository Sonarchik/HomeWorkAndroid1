package com.example.hwone.userdto

import android.content.Context
import android.content.SharedPreferences
import com.example.hwone.Constants.EXTRA_EMAIL
import com.example.hwone.Constants.EXTRA_PASSWORD
import com.example.hwone.Constants.EXTRA_REMEMBER
import com.example.hwone.Constants.USER_PREFS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserPreferencesSharedPrefs(private val context: Context) : UserPreferencesInterface {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)

    // Private function to retrieve a value from SharedPreferences and emit it as Flow
    private fun <T> getValueFlow(key: String, defaultValue: T): Flow<T> = flow {
        emit(
            when (defaultValue) {
                is String -> sharedPreferences.getString(key, defaultValue) as T
                is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
                else -> throw IllegalArgumentException("Unsupported type")
            }
        )
    }

    // Private function to save a value in SharedPreferences
    private fun <T> saveValue(key: String, value: T) {
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
            }
            apply()
        }
    }

    override suspend fun saveLoginData(email: String, password: String, rememberMe: Boolean) {
        saveValue(EXTRA_EMAIL, email)
        saveValue(EXTRA_PASSWORD, if (rememberMe) password else "")
        saveValue(EXTRA_REMEMBER, rememberMe)
    }

    override suspend fun isRememberMeChecked(): Flow<Boolean> {
        return getValueFlow(EXTRA_REMEMBER, false)
    }

    override suspend fun getEmail(): Flow<String?> {
        return getValueFlow(EXTRA_EMAIL, "")
    }

    override suspend fun getPassword(): Flow<String?> {
        return getValueFlow(EXTRA_PASSWORD, "")
    }

    override suspend fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}