package com.example.proyectoseminario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registro_respuestas")
data class RegistroRespuesta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ejercicioId: Int,
    val nodoId: Int,
    val esCorrecto: Boolean,
    val tiempoSegundos: Int,
    val fechaTimestamp: Long = System.currentTimeMillis()
)