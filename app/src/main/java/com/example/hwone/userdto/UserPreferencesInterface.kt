package com.example.hwone.userdto

import kotlinx.coroutines.flow.Flow

interface UserPreferencesInterface {
    suspend fun saveLoginData(email: String, password: String,rememberMe:Boolean)
    suspend fun isRememberMeChecked(): Flow<Boolean?>
    suspend fun getEmail(): Flow<String?>
    suspend fun getPassword(): Flow<String?>
    suspend fun logout()
}