package com.example.proyectoseminario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nodos_camino")
data class NodoCamino(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val areaMatematica: String, // Ej: "Álgebra", "Cálculo", "Geometría"
    val nivelOrden: Int,       // Posición en el mapa
    val estaDesbloqueado: Boolean = false,
    val estaCompletado: Boolean = false,
    val nodoPrerrequisitoId: Int? = null // Para la lógica de retroceder o bloquear
)