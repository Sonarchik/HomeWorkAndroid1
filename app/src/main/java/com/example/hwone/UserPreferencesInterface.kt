package com.example.hwone

interface UserPreferencesInterface {
    suspend fun saveLoginData(email: String)
    suspend fun isLoggedIn(): Boolean
    suspend fun getEmail(): String?
    suspend fun logout()
}
