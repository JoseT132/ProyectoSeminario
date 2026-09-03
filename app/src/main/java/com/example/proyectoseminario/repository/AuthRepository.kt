package com.example.proyectoseminario.repository

import com.example.proyectoseminario.data.local.AppDao
import com.example.proyectoseminario.data.local.PerfilUsuario
import com.example.proyectoseminario.utils.SecurityUtils
import kotlinx.coroutines.flow.firstOrNull

class AuthRepository(private val appDao: AppDao) {

    suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        password: String,
        edad: Int,
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
            edad = edad,
            nivelEscolar = nivelEscolar,
            esLocal = false,
            puntos = 0,
            rachaDias = 0
        )

        appDao.insertPerfil(perfil)
        return Result.success(perfil)
    }

    suspend fun registrarUsuarioLocal(
        nombre: String,
        edad: Int,
        nivelEscolar: String
    ): Result<PerfilUsuario> {
        val perfil = PerfilUsuario(
            nombre = nombre,
            edad = edad,
            nivelEscolar = nivelEscolar,
            esLocal = true,
            puntos = 0,
            rachaDias = 0
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

        val hash = perfil.passwordHash
            ?: return Result.failure(Exception("Esta cuenta no tiene contraseña"))

        if (!SecurityUtils.verifyPassword(password, hash)) {
            return Result.failure(Exception("Contraseña incorrecta"))
        }

        return Result.success(perfil)
    }

    suspend fun getPerfilPorId(id: Int): PerfilUsuario? {
        val perfiles = appDao.getPerfil().firstOrNull()
        return perfiles
    }

    suspend fun getPrimerPerfil(): PerfilUsuario? {
        return appDao.getPrimerPerfil()
    }

    suspend fun perfilExiste(correo: String): Boolean {
        return appDao.existeCorreo(correo) > 0
    }
}
