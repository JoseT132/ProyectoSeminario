package com.example.proyectoseminario.ui.ejercicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.data.local.BancoEjercicios
import com.example.proyectoseminario.data.local.Ejercicio
import com.example.proyectoseminario.repository.MapaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EjercicioViewModel(
    private val mapaRepository: MapaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EjercicioUiState())
    val uiState: StateFlow<EjercicioUiState> = _uiState.asStateFlow()

    fun cargarEjercicios(nodoId: Int) {
        _uiState.value = EjercicioUiState(isLoading = true)
        viewModelScope.launch {
            val sesion = mapaRepository.obtenerEjerciciosSesion(
                nodoId,
                BancoEjercicios.EJERCICIOS_POR_SESION
            )
            _uiState.value = EjercicioUiState(
                isLoading = false,
                ejercicios = sesion,
                nodoId = nodoId,
                tiempoInicio = System.currentTimeMillis()
            )
        }
    }

    fun seleccionarOpcion(index: Int) {
        val state = _uiState.value
        if (state.esCorrecto == null) {
            _uiState.value = state.copy(opcionSeleccionada = index)
        }
    }

    fun verificarRespuesta() {
        val state = _uiState.value
        val ejercicio = state.ejercicioActual ?: return
        val seleccion = state.opcionSeleccionada ?: return

        val correcta = seleccion == ejercicio.respuestaCorrecta
        val tiempoSegundos = ((System.currentTimeMillis() - state.tiempoInicio) / 1000).toInt()

        viewModelScope.launch {
            mapaRepository.guardarRespuesta(
                nodoId = ejercicio.nodoId,
                ejercicioId = ejercicio.id,
                esCorrecto = correcta,
                tiempoSegundos = tiempoSegundos
            )
        }

        _uiState.value = state.copy(
            esCorrecto = correcta,
            aciertos = if (correcta) state.aciertos + 1 else state.aciertos,
            respondidas = state.respondidas + 1,
            tiempoSegundos = tiempoSegundos
        )
    }

    fun siguiente() {
        val state = _uiState.value
        val siguienteIndice = state.indiceActual + 1

        if (siguienteIndice >= state.ejercicios.size) {
            _uiState.value = state.copy(
                finalizado = true,
                dominioAlcanzado = state.aciertos >= ACIERTOS_PARA_APROBAR
            )
            return
        }

        _uiState.value = state.copy(
            indiceActual = siguienteIndice,
            opcionSeleccionada = null,
            esCorrecto = null,
            tiempoInicio = System.currentTimeMillis(),
            tiempoSegundos = 0
        )
    }

    fun reiniciar() {
        val state = _uiState.value
        _uiState.value = EjercicioUiState(isLoading = true)
        viewModelScope.launch {
            val sesion = mapaRepository.obtenerEjerciciosSesion(
                state.nodoId,
                BancoEjercicios.EJERCICIOS_POR_SESION
            )
            _uiState.value = EjercicioUiState(
                isLoading = false,
                ejercicios = sesion,
                nodoId = state.nodoId,
                tiempoInicio = System.currentTimeMillis()
            )
        }
    }

    companion object {
        const val ACIERTOS_PARA_APROBAR = 4 // 4 de 5 = 80%
    }

    data class EjercicioUiState(
        val isLoading: Boolean = false,
        val ejercicios: List<Ejercicio> = emptyList(),
        val nodoId: Int = 0,
        val indiceActual: Int = 0,
        val opcionSeleccionada: Int? = null,
        val esCorrecto: Boolean? = null,
        val tiempoSegundos: Int = 0,
        val tiempoInicio: Long = 0,
        val aciertos: Int = 0,
        val respondidas: Int = 0,
        val finalizado: Boolean = false,
        val dominioAlcanzado: Boolean = false
    ) {
        val ejercicioActual: Ejercicio? get() = ejercicios.getOrNull(indiceActual)
        val total: Int get() = ejercicios.size
        val progreso: String get() = "Ejercicio ${indiceActual + 1} de $total"
        val progresoFraccion: Float get() =
            if (total > 0) respondidas.toFloat() / total.toFloat() else 0f
    }
}
