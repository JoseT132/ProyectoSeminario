package com.example.proyectoseminario.repository

import com.example.proyectoseminario.data.local.AppDao
import com.example.proyectoseminario.data.local.PerfilUsuario
import com.example.proyectoseminario.utils.SecurityUtils
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val appDao: AppDao,
    private val syncRepository: SyncRepository? = null
) {

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

        // Registro dual: el usuario también se crea en Firebase Auth para poder
        // enviarle el correo de restablecimiento de contraseña.
        var firebaseUid: String? = null
        try {
            firebaseUid = Firebase.auth
                .createUserWithEmailAndPassword(correo, password).await()
                .user?.uid
        } catch (_: Exception) {
            // Sin conexión o el correo ya existe en Firebase: se crea solo local.
        }

        val perfil = PerfilUsuario(
            nombre = nombre,
            correo = correo,
            passwordHash = SecurityUtils.hashPassword(password),
            firebaseUid = firebaseUid,
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

        val perfilLocal = appDao.getPerfilPorCorreo(correo)
        if (perfilLocal != null &&
            (perfilLocal.proveedorAuth == "google" || perfilLocal.passwordHash.isBlank())
        ) {
            return Result.failure(Exception("Esta cuenta usa Google. Inicia con 'Continuar con Google'"))
        }

        // Primero Firebase: necesario para que una contraseña restablecida por
        // correo funcione aunque el hash local todavía sea el antiguo.
        try {
            val firebaseUser = Firebase.auth
                .signInWithEmailAndPassword(correo, password).await().user
                ?: return Result.failure(Exception("No se pudo iniciar sesión"))

            var perfil = perfilLocal
            if (perfil == null) {
                appDao.insertPerfil(
                    PerfilUsuario(
                        nombre = firebaseUser.displayName ?: correo.substringBefore("@"),
                        correo = correo,
                        passwordHash = SecurityUtils.hashPassword(password),
                        firebaseUid = firebaseUser.uid
                    )
                )
                perfil = appDao.getPerfilPorCorreo(correo)
            } else {
                // Resincroniza el hash local con la contraseña actual de Firebase
                perfil = perfil.copy(
                    passwordHash = SecurityUtils.hashPassword(password),
                    firebaseUid = firebaseUser.uid
                )
                appDao.updatePerfil(perfil)
            }

            // Sincronizar con la nube: restaura progreso previo y asegura el documento.
            syncRepository?.restaurarProgreso(firebaseUser.uid)
            syncRepository?.subirProgreso()

            return perfil?.let { Result.success(it) }
                ?: Result.failure(Exception("No se pudo crear el perfil"))
        } catch (e: FirebaseAuthException) {
            when (e.errorCode) {
                // Cuenta deshabilitada o eliminada desde la consola: bloquear y
                // limpiar el perfil local si estaba vinculada a Firebase.
                "ERROR_USER_DISABLED" ->
                    return Result.failure(Exception("Esta cuenta está deshabilitada. Contacta al administrador"))
                "ERROR_USER_NOT_FOUND", "ERROR_USER_DELETED" -> {
                    perfilLocal?.firebaseUid?.let { appDao.deletePerfil(perfilLocal.id) }
                    return Result.failure(Exception("Esta cuenta fue eliminada"))
                }
                // La cuenta existe en Firebase pero la contraseña no coincide:
                // Firebase es autoritativo, no caer al respaldo local.
                "ERROR_INVALID_LOGIN_CREDENTIALS" ->
                    if (perfilLocal?.firebaseUid != null) {
                        return Result.failure(Exception("Contraseña incorrecta"))
                    }
            }
            // Cuenta solo local o error de red: continuar con verificación local.
        } catch (_: Exception) {
            // Sin conexión: continuar con verificación local.
        }

        val perfil = perfilLocal
            ?: return Result.failure(Exception("No existe una cuenta con este correo"))

        if (!SecurityUtils.verifyPassword(password, perfil.passwordHash)) {
            return Result.failure(Exception("Contraseña incorrecta"))
        }

        return Result.success(perfil)
    }

    /**
     * Envía el correo de restablecimiento de contraseña de Firebase Auth.
     * Solo funciona para cuentas registradas también en Firebase.
     */
    suspend fun enviarCorreoRecuperacion(correo: String): Result<Unit> {
        if (!SecurityUtils.isValidEmail(correo)) {
            return Result.failure(Exception("El correo no tiene un formato válido"))
        }
        return try {
            Firebase.auth.sendPasswordResetEmail(correo).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            val mensaje = when (e.errorCode) {
                "ERROR_USER_NOT_FOUND", "ERROR_INVALID_LOGIN_CREDENTIALS" ->
                    "No existe una cuenta registrada con este correo"
                "ERROR_INVALID_EMAIL" -> "El correo no tiene un formato válido"
                else -> "No se pudo enviar el correo de recuperación"
            }
            Result.failure(Exception(mensaje))
        } catch (_: Exception) {
            Result.failure(Exception("Sin conexión. Verifica tu internet e inténtalo de nuevo"))
        }
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

            // Restaurar progreso de la nube (otro dispositivo) y asegurar el documento.
            syncRepository?.restaurarProgreso(firebaseUser.uid)
            syncRepository?.subirProgreso()

            perfil?.let { Result.success(it) }
                ?: Result.failure(Exception("No se pudo crear el perfil"))
        } catch (e: Exception) {
            Result.failure(Exception("Error con Google Sign-In: ${e.message}"))
        }
    }

    suspend fun perfilExiste(correo: String): Boolean {
        return appDao.existeCorreo(correo) > 0
    }

    /**
     * Elimina la cuenta completa: perfil local, documento de progreso en
     * Firestore y usuario de Firebase Auth.
     */
    suspend fun eliminarCuenta(id: Int) {
        val perfil = appDao.getPrimerPerfil().firstOrNull()
        val uid = perfil?.firebaseUid ?: Firebase.auth.currentUser?.uid

        appDao.deletePerfil(id)

        uid?.let { syncRepository?.eliminarProgreso(it) }

        try {
            val actual = Firebase.auth.currentUser
            if (actual != null && (uid == null || actual.uid == uid)) {
                actual.delete().await()
            }
        } catch (_: Exception) {
            // Requiere login reciente: el perfil local ya se borró; el usuario
            // de Firebase puede eliminarse desde la consola.
        }
        try {
            Firebase.auth.signOut()
        } catch (_: Exception) {
        }
    }
}
