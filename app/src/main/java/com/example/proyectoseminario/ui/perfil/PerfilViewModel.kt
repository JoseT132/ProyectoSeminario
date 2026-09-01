package com.example.proyectoseminario.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.data.local.PerfilUsuario
import com.example.proyectoseminario.repository.MapaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class PerfilUiState(
    val perfil: PerfilUsuario? = null,
    val totalRespondidas: Int = 0,
    val totalCorrectas: Int = 0,
    val precisionPorcentaje: Int = 0
)

class PerfilViewModel(
    private val mapaRepository: MapaRepository
) : ViewModel() {

    val uiState: StateFlow<PerfilUiState> = combine(
        mapaRepository.getPerfil(),
        mapaRepository.getTotalRespuestas(),
        mapaRepository.getTotalRespuestasCorrectas()
    ) { perfil, total, correctas ->
        val precision = if (total > 0) ((correctas.toDouble() / total) * 100).toInt() else 0
        PerfilUiState(
            perfil = perfil,
            totalRespondidas = total,
            totalCorrectas = correctas,
            precisionPorcentaje = precision
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PerfilUiState()
    )
}