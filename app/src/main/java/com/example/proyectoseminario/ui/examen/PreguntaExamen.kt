package com.example.proyectoseminario.ui.examen

data class PreguntaExamen(
    val id: Int,
    val enunciado: String,
    val opciones: List<String>,
    val respuestaCorrecta: Int,
    val area: String
)
