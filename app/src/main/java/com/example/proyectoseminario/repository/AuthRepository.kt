package com.example.proyectoseminario.repository

import com.example.proyectoseminario.data.local.AppDao
import com.example.proyectoseminario.data.local.PerfilUsuario
import com.example.proyectoseminario.utils.SecurityUtils
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await

class AuthRepository(private val appDao: AppDao) {

    suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        password: String,
        fechaNacimiento: String,
        nivelEscolar: String
    ): Result<PerfilUsuario> {
        if (!SecurityUtils.isValidEmail(correo)) {
            return Result.failure(Exception("El correo no tiene un formato válido"))
        }

        val existe = appDao.existeCorreo(correo) > 0
        if (existe) {
            return Result.failure(Exception("Ya existe una cuenta con este correo"))
        }

        val perfil = PerfilUsuario(
            nombre = nombre,
            correo = correo,
            passwordHash = SecurityUtils.hashPassword(password),
            fechaNacimiento = fechaNacimiento,
            nivelEscolar = nivelEscolar
        )

        appDao.insertPerfil(perfil)
        return Result.success(perfil)
    }

    suspend fun iniciarSesion(correo: String, password: String): Result<PerfilUsuario> {
        if (!SecurityUtils.isValidEmail(correo)) {
            return Result.failure(Exception("El correo no tiene un formato válido"))
        }

        val perfil = appDao.getPerfilPorCorreo(correo)
            ?: return Result.failure(Exception("No existe una cuenta con este correo"))

        if (perfil.proveedorAuth == "google" || perfil.passwordHash.isBlank()) {
            return Result.failure(Exception("Esta cuenta usa Google. Inicia con 'Continuar con Google'"))
        }

        if (!SecurityUtils.verifyPassword(password, perfil.passwordHash)) {
            return Result.failure(Exception("Contraseña incorrecta"))
        }

        return Result.success(perfil)
    }

    /**
     * Autentica el idToken de Google contra Firebase y crea el perfil local
     * si es la primera vez que el usuario ingresa.
     */
    suspend fun iniciarSesionConGoogle(idToken: String): Result<PerfilUsuario> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = Firebase.auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("No se obtuvo el usuario de Google"))

            val correo = firebaseUser.email
                ?: return Result.failure(Exception("La cuenta de Google no tiene correo"))

            var perfil = appDao.getPerfilPorCorreo(correo)
            if (perfil == null) {
                appDao.insertPerfil(
                    PerfilUsuario(
                        nombre = firebaseUser.displayName ?: correo.substringBefore("@"),
                        correo = correo,
                        passwordHash = "",
                        proveedorAuth = "google",
                        firebaseUid = firebaseUser.uid
                    )
                )
                perfil = appDao.getPerfilPorCorreo(correo)
            } else if (perfil.firebaseUid == null) {
                perfil = perfil.copy(
                    proveedorAuth = "google",
                    firebaseUid = firebaseUser.uid
                )
                appDao.updatePerfil(perfil)
            }

            perfil?.let { Result.success(it) }
                ?: Result.failure(Exception("No se pudo crear el perfil"))
        } catch (e: Exception) {
            Result.failure(Exception("Error con Google Sign-In: ${e.message}"))
        }
    }

    suspend fun perfilExiste(correo: String): Boolean {
        return appDao.existeCorreo(correo) > 0
    }

    suspend fun eliminarCuenta(id: Int) {
        appDao.deletePerfil(id)
    }
}
