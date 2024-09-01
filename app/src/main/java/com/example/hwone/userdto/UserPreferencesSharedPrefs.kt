package com.example.hwone.userdto

import android.content.Context
import android.content.SharedPreferences
import com.example.hwone.Constants
import com.example.hwone.Constants.EXTRA_EMAIL
import com.example.hwone.Constants.EXTRA_PASSWORD
import com.example.hwone.Constants.EXTRA_REMEMBER
import com.example.hwone.Constants.IS_LOGGED_IN
import com.example.hwone.Constants.USER_PREFS

class UserPreferencesSharedPrefs(private val context: Context) : UserPreferencesInterface {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)

    // Private function to retrieve a value from SharedPreferences
    private fun <T> getValue(key: String, defaultValue: T? = null): T? {
        return when (defaultValue) {
            is String? -> sharedPreferences.getString(key, defaultValue as String?) as T?
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T?
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    // Private function to save a value in SharedPreferences
    private fun <T> saveValue(key: String, value: T) {
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
            apply()
        }
    }

    override suspend fun saveLoginData(email: String, password: String, rememberMe: Boolean) {
        saveValue(EXTRA_EMAIL, email)
        saveValue(EXTRA_PASSWORD, if (rememberMe) password else "")
        saveValue(EXTRA_REMEMBER, rememberMe)
        saveValue(IS_LOGGED_IN, rememberMe)
    }

    override suspend fun isLoggedIn(): Boolean {
        return getValue(IS_LOGGED_IN, false) ?: false
    }

    override suspend fun isRememberMeChecked(): Boolean {
        return getValue(EXTRA_REMEMBER, false) ?: false
    }

    override suspend fun getEmail(): String? {
        return getValue(EXTRA_EMAIL)
    }

    override suspend fun getPassword(): String? {
        return getValue(EXTRA_PASSWORD)
    }

    override suspend fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}
