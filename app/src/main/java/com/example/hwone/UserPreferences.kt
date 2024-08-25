package com.example.hwone

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveLoginData(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}