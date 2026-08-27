package com.example.proyectoseminario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil_usuario")
data class PerfilUsuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val nivelActual: Int = 1,
    val puntos: Int = 0,
    val rachaDias: Int = 0,
    val precisionGeneral: Float = 0.0f
)