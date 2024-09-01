package com.example.hwone.userdto

interface UserPreferencesInterface {
    suspend fun saveLoginData(email: String, password: String,rememberMe:Boolean)
    suspend fun isLoggedIn(): Boolean
    suspend fun isRememberMeChecked(): Boolean
    suspend fun getEmail(): String?
    suspend fun getPassword(): String?
    suspend fun logout()
}