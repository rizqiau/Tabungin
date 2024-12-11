package com.example.ones.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.savingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "savings_session")

class SavingsPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSavingsData(savingId: String) {
        dataStore.edit { preferences ->
            preferences[SAVING_ID_KEY] = savingId
        }
        Log.d("SavingsPreference", "SavingId saved: $savingId")
    }

    fun getSavingId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            val savingId = preferences[SAVING_ID_KEY]
            if (savingId.isNullOrEmpty()) {
                Log.d("SavingsPreference", "No SavingId found.")
            } else {
                Log.d("SavingsPreference", "SavingId fetched: $savingId")
            }
            savingId
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SavingsPreference? = null

        private val SAVING_ID_KEY = stringPreferencesKey("savingId")

        fun getInstance(dataStore: DataStore<Preferences>): SavingsPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SavingsPreference(dataStore).also { INSTANCE = it }
            }
        }
    }
}
