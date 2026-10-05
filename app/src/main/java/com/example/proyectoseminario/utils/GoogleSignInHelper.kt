package com.example.proyectoseminario.utils

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.proyectoseminario.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

object GoogleSignInHelper {

    /**
     * Lanza el flujo de Google Sign-In vía Credential Manager y devuelve el
     * ID token para autenticar contra Firebase. Requiere el Web Client ID
     * configurado en strings.xml (google_web_client_id) y google-services.json.
     */
    suspend fun obtenerIdToken(context: Context): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.google_web_client_id))
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
            Result.success(credential.idToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Cierra la sesión de Firebase y limpia el estado de credenciales. */
    suspend fun cerrarSesion(context: Context) {
        try {
            Firebase.auth.signOut()
        } catch (_: Exception) {
            // Firebase no inicializado (sin google-services.json): ignorar
        }
        try {
            CredentialManager.create(context)
                .clearCredentialState(ClearCredentialStateRequest())
        } catch (_: Exception) {
            // Sin credenciales de Google que limpiar: ignorar
        }
    }
}
