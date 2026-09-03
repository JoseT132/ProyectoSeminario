package com.example.proyectoseminario.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "perfil_usuario",
    indices = [Index(value = ["correo"], unique = true)]
)
data class PerfilUsuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val correo: String? = null,
    val passwordHash: String? = null,
    val edad: Int? = null,
    val nivelEscolar: String = "",
    val nivelActual: Int = 1,
    val puntos: Int = 0,
    val rachaDias: Int = 0,
    val precisionGeneral: Float = 0.0f,
    val esLocal: Boolean = true,
    val fechaRegistro: Long = System.currentTimeMillis()
)