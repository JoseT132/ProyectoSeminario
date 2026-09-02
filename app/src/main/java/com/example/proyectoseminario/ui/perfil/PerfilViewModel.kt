package com.example.proyectoseminario.ui.perfil

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.repository.MapaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PerfilViewModel(
    private val repository: MapaRepository
) : ViewModel() {

    val uiState: StateFlow<PerfilUiState> = combine(
        repository.getPerfil(),
        repository.getTotalRespuestas(),
        repository.getTotalRespuestasCorrectas(),
        repository.getTodosLosNodos()
    ) { perfil, totalRespuestas, totalCorrectas, listaNodos ->
        val precision = if (totalRespuestas > 0) (totalCorrectas * 100) / totalRespuestas else 0
        val puntos = perfil?.puntos ?: 0
        val racha = perfil?.rachaDias ?: 0

        val totalNodos = listaNodos.size
        val nodosCompletados = listaNodos.count { it.estaCompletado }

        val listaLogros = listOf(
            Logro(
                id = "primer_paso",
                titulo = "Primer Paso",
                descripcion = "Resuelve tu primer ejercicio",
                icono = Icons.Default.ThumbUp,
                desbloqueado = totalRespuestas >= 1
            ),
            Logro(
                id = "racha_fuego",
                titulo = "Constancia",
                descripcion = "Alcanza una racha de 3 días",
                icono = Icons.Default.DateRange,
                desbloqueado = racha >= 3
            ),
            Logro(
                id = "cien_puntos",
                titulo = "Centenario",
                descripcion = "Acumula 100 puntos en total",
                icono = Icons.Default.Star,
                desbloqueado = puntos >= 100
            )
        )

        PerfilUiState(
            perfil = perfil,
            totalRespuestas = totalRespuestas,
            totalCorrectas = totalCorrectas,
            precisionPorcentaje = precision,
            nodosCompletados = nodosCompletados,
            totalNodos = totalNodos,
            logros = listaLogros,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PerfilUiState()
    )
}