package com.example.proyectoseminario.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_preferences")

class SessionManager(private val context: Context) {

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_ID = intPreferencesKey("user_id")
        val RACHA_DIAS = intPreferencesKey("racha_dias")
        val LAST_LOGIN_DATE = stringPreferencesKey("last_login_date")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val currentUserEmail: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL]
        }

    val currentUserId: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID] ?: 0
        }

    suspend fun saveSession(userId: Int, email: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = name
        }
    }

    suspend fun actualizarRacha(): Int {
        val hoy = LocalDate.now().toString()
        val prefs = context.dataStore.data.firstOrNull()
        val ultimaFecha = prefs?.get(LAST_LOGIN_DATE)
        val rachaActual = prefs?.get(RACHA_DIAS) ?: 0

        val nuevaRacha = when {
            ultimaFecha == null || ultimaFecha == hoy -> rachaActual.coerceAtLeast(1)
            ChronoUnit.DAYS.between(LocalDate.parse(ultimaFecha), LocalDate.now()) == 1L -> rachaActual + 1
            ChronoUnit.DAYS.between(LocalDate.parse(ultimaFecha), LocalDate.now()) < 1L -> rachaActual
            else -> 1
        }

        context.dataStore.edit { preferences ->
            preferences[RACHA_DIAS] = nuevaRacha
            preferences[LAST_LOGIN_DATE] = hoy
        }

        return nuevaRacha
    }

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HAS_COMPLETED_ONBOARDING] ?: false
        }

    suspend fun hasCompletedOnboarding(): Boolean = withContext(Dispatchers.IO) {
        context.dataStore.data.firstOrNull()?.get(HAS_COMPLETED_ONBOARDING) ?: false
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[HAS_COMPLETED_ONBOARDING] = true
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences.remove(USER_ID)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_NAME)
        }
    }
}
