package com.example.proyectoseminario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ejercicios")
data class Ejercicio(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nodoId: Int,             // Relación con el NodoCamino
    val enunciado: String,        // Pregunta o problema
    val opcionA: String,
    val opcionB: String,
    val opcionC: String,
    val opcionD: String,
    val respuestaCorrecta: Int,   // Índice 0, 1, 2 o 3
    val dificultad: Int,          // Nivel 1 (Fácil), 2 (Medio), 3 (Díficil)
    val explicacion: String       // Paso a paso pedagógico para cuando falle
)