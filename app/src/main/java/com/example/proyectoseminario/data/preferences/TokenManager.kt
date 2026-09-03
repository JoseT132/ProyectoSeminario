package com.example.proyectoseminario.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "token_preferences")

class TokenManager(private val context: Context) {

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val EXPIRES_AT = longPreferencesKey("expires_at")
    }

    val accessToken: Flow<String?> = context.tokenDataStore.data
        .map { it[ACCESS_TOKEN] }

    val refreshToken: Flow<String?> = context.tokenDataStore.data
        .map { it[REFRESH_TOKEN] }

    suspend fun saveTokens(access: String, refresh: String, expiresAt: Long) {
        context.tokenDataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = access
            prefs[REFRESH_TOKEN] = refresh
            prefs[EXPIRES_AT] = expiresAt
        }
    }

    suspend fun clearTokens() {
        context.tokenDataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
            prefs.remove(EXPIRES_AT)
        }
    }

    suspend fun getAccessToken(): String? {
        return context.tokenDataStore.data.map { it[ACCESS_TOKEN] }.firstOrNull()
    }
}
