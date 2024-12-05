package com.example.ones.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import com.example.ones.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences as DataStorePreferences

val Context.dataStore: DataStore<DataStorePreferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<DataStorePreferences>) {
    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = user.username // Simpan username
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[USER_ID_KEY] = user.userId
            preferences[IS_LOGIN_KEY] = true
        }
    }


    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[USERNAME_KEY] ?: "",  // Mengambil username dari preferences
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[USER_ID_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }


    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = ""
            preferences[TOKEN_KEY] = ""
            preferences[USER_ID_KEY] = ""
            preferences[IS_LOGIN_KEY] = false
        }
    }
    suspend fun getToken(): String? {
        val preferences = dataStore.data.first()
        return preferences[TOKEN_KEY]
    }

    suspend fun getUserId(): String? {
        val preferences = dataStore.data.first()
        return preferences[USER_ID_KEY]
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USERNAME_KEY = stringPreferencesKey("username")  // Menambahkan KEY untuk username
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USER_ID_KEY = stringPreferencesKey("userId")
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
