package com.example.hwone

import android.content.Context
import android.content.SharedPreferences

class UserPreferencesSharedPrefs(private val context: Context) : UserPreferencesInterface {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    override suspend fun saveLoginData(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    override suspend fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    override suspend fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    override suspend fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
