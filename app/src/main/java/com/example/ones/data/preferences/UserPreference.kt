package com.example.ones.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import com.example.ones.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences as DataStorePreferences

val Context.dataStore: DataStore<DataStorePreferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<DataStorePreferences>) {
    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[USER_ID_KEY] = user.userId
            preferences[IS_LOGIN_KEY] = true
            Log.d("UserPreference", "Session saved: userId=${user.userId}, token=${user.token}")
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            val userId = preferences[USER_ID_KEY] ?: ""
            val token = preferences[TOKEN_KEY] ?: ""
            Log.d("UserPreference", "Session fetched: userId=$userId, token=$token")
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                token,
                userId,
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[IS_LOGIN_KEY] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<DataStorePreferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
