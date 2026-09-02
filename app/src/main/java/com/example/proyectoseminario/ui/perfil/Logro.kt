package com.example.proyectoseminario.ui.perfil

import androidx.compose.ui.graphics.vector.ImageVector

data class Logro(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val desbloqueado: Boolean
)