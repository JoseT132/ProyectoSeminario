package com.example.proyectoseminario.ui.perfil

import com.example.proyectoseminario.data.local.PerfilUsuario

data class PerfilUiState(
    val perfil: PerfilUsuario? = null,
    val totalRespuestas: Int = 0,
    val totalCorrectas: Int = 0,
    val precisionPorcentaje: Int = 0,
    val nodosCompletados: Int = 0,
    val totalNodos: Int = 0,
    val logros: List<Logro> = emptyList(),
    val isLoading: Boolean = true
)