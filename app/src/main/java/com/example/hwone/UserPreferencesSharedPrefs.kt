package com.example.hwone

import android.content.Context
import android.content.SharedPreferences
import com.example.hwone.Constants.EXTRA_EMAIL
import com.example.hwone.Constants.IS_LOGGED_IN
import com.example.hwone.Constants.USER_PREFS

class UserPreferencesSharedPrefs(private val context: Context) : UserPreferencesInterface {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)

    override suspend fun saveLoginData(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString(EXTRA_EMAIL, email)
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.apply()
    }

    override suspend fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    override suspend fun getEmail(): String? {
        return sharedPreferences.getString(EXTRA_EMAIL, null)
    }

    override suspend fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
