package com.example.proyectoseminario.ui.ejercicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            val ejercicios = mapaRepository.obtenerEjerciciosPorNodo(nodoId)
            _uiState.value = EjercicioUiState(
                isLoading = false,
                ejercicios = ejercicios,
                nodoId = nodoId
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
        val aciertos = if (correcta) state.aciertos + 1 else state.aciertos

        viewModelScope.launch {
            mapaRepository.guardarRespuesta(
                nodoId = ejercicio.nodoId,
                ejercicioId = ejercicio.id,
                esCorrecto = correcta
            )
        }

        _uiState.value = state.copy(
            esCorrecto = correcta,
            aciertos = aciertos
        )
    }

    fun siguiente() {
        val state = _uiState.value
        val indice = state.indiceActual

        if (indice < state.ejercicios.size - 1) {
            _uiState.value = state.copy(
                indiceActual = indice + 1,
                opcionSeleccionada = null,
                esCorrecto = null
            )
        } else {
            val total = state.ejercicios.size
            val dominio = if (total > 0) (state.aciertos * 100) / total else 0
            _uiState.value = state.copy(
                dominioAlcanzado = dominio >= 80,
                finalizado = true
            )
        }
    }

    fun reiniciar() {
        _uiState.value = EjercicioUiState(
            isLoading = false,
            ejercicios = _uiState.value.ejercicios,
            nodoId = _uiState.value.nodoId
        )
    }

    data class EjercicioUiState(
        val isLoading: Boolean = false,
        val ejercicios: List<Ejercicio> = emptyList(),
        val nodoId: Int = 0,
        val indiceActual: Int = 0,
        val opcionSeleccionada: Int? = null,
        val esCorrecto: Boolean? = null,
        val aciertos: Int = 0,
        val finalizado: Boolean = false,
        val dominioAlcanzado: Boolean = false
    ) {
        val ejercicioActual: Ejercicio? get() = ejercicios.getOrNull(indiceActual)
        val progreso: String get() = "Pregunta ${indiceActual + 1} de ${ejercicios.size}"
    }
}