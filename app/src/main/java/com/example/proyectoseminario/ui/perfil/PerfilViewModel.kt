package com.example.proyectoseminario.ui.perfil

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.proyectoseminario.data.local.BancoEjercicios
import com.example.proyectoseminario.data.preferences.SessionManager
import com.example.proyectoseminario.repository.MapaRepository
import com.example.proyectoseminario.utils.GoogleSignInHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: MapaRepository,
    private val sessionManager: SessionManager
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

        val desafiosCompletados = listaNodos.count {
            it.tipo == BancoEjercicios.TIPO_APLICADO && it.estaCompletado
        }
        val jefesDerrotados = listaNodos.count {
            it.tipo == BancoEjercicios.TIPO_BOSS && it.estaCompletado
        }
        val jefeFinalDerrotado = listaNodos.any {
            it.tipo == BancoEjercicios.TIPO_BOSS_FINAL && it.estaCompletado
        }

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
            ),
            Logro(
                id = "primer_desafio",
                titulo = "Retador",
                descripcion = "Supera tu primer desafío aplicado 🐉",
                icono = Icons.Default.PlayArrow,
                desbloqueado = desafiosCompletados >= 1
            ),
            Logro(
                id = "cazador_dragones",
                titulo = "Cazador de Dragones",
                descripcion = "Supera los 6 desafíos aplicados",
                icono = Icons.Default.Star,
                desbloqueado = desafiosCompletados >= 6
            ),
            Logro(
                id = "matagigantes",
                titulo = "Matagigantes",
                descripcion = "Derrota a tu primer mini-jefe 💀",
                icono = Icons.Default.CheckCircle,
                desbloqueado = jefesDerrotados >= 1
            ),
            Logro(
                id = "leyenda_reino",
                titulo = "Leyenda del Reino",
                descripcion = "Derrota al Dragón del Caos, el Jefe Final 👑",
                icono = Icons.Default.Face,
                desbloqueado = jefeFinalDerrotado
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

    fun cerrarSesion(context: Context, onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            GoogleSignInHelper.cerrarSesion(context)
            sessionManager.clearSession()
            onLogoutComplete()
        }
    }
}