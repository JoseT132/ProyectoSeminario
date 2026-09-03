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
            val primerEjercicio = ejercicios
                .filter { it.dificultad == 1 }
                .minByOrNull { it.id }
            val ruta = if (primerEjercicio != null) listOf(primerEjercicio) else emptyList()
            _uiState.value = EjercicioUiState(
                isLoading = false,
                ejercicios = ejercicios,
                ruta = ruta,
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
        val aciertos = if (correcta) state.aciertos + 1 else state.aciertos
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
            aciertos = aciertos,
            tiempoSegundos = tiempoSegundos
        )
    }

    fun siguiente() {
        val state = _uiState.value
        val respondidas = state.respondidas + 1
        val dificultad = siguienteDificultad(state)

        if (respondidas >= META_PREGUNTAS) {
            val dominio = if (respondidas > 0) (state.aciertos * 100) / respondidas else 0
            _uiState.value = state.copy(
                respondidas = respondidas,
                dominioAlcanzado = dominio >= 80,
                finalizado = true
            )
            return
        }

        val siguienteEjercicio = state.ejercicios
            .filter { it.dificultad == dificultad }
            .minByOrNull { it.id }
            ?: state.ejercicios.lastOrNull()

        val nuevaRuta = if (siguienteEjercicio != null) state.ruta + siguienteEjercicio else state.ruta
        _uiState.value = state.copy(
            ruta = nuevaRuta,
            indiceActual = nuevaRuta.size - 1,
            respondidas = respondidas,
            dificultadActual = dificultad,
            opcionSeleccionada = null,
            esCorrecto = null,
            tiempoInicio = System.currentTimeMillis(),
            tiempoSegundos = 0
        )
    }

    fun reiniciar() {
        val state = _uiState.value
        val primerEjercicio = state.ejercicios
            .filter { it.dificultad == 1 }
            .minByOrNull { it.id }
        val ruta = if (primerEjercicio != null) listOf(primerEjercicio) else emptyList()
        _uiState.value = EjercicioUiState(
            isLoading = false,
            ejercicios = state.ejercicios,
            ruta = ruta,
            nodoId = state.nodoId,
            tiempoInicio = System.currentTimeMillis()
        )
    }

    private fun siguienteDificultad(state: EjercicioUiState): Int {
        val esCorrecto = state.esCorrecto ?: return 1
        val tiempo = state.tiempoSegundos
        val actual = state.dificultadActual
        return when {
            esCorrecto && tiempo <= 10 -> (actual + 1).coerceAtMost(20)
            esCorrecto -> actual
            else -> (actual - 1).coerceAtLeast(1)
        }
    }

    companion object {
        const val META_PREGUNTAS = 20
    }

    data class EjercicioUiState(
        val isLoading: Boolean = false,
        val ejercicios: List<Ejercicio> = emptyList(),
        val ruta: List<Ejercicio> = emptyList(),
        val nodoId: Int = 0,
        val indiceActual: Int = 0,
        val dificultadActual: Int = 1,
        val opcionSeleccionada: Int? = null,
        val esCorrecto: Boolean? = null,
        val tiempoSegundos: Int = 0,
        val tiempoInicio: Long = 0,
        val aciertos: Int = 0,
        val respondidas: Int = 0,
        val finalizado: Boolean = false,
        val dominioAlcanzado: Boolean = false
    ) {
        val ejercicioActual: Ejercicio? get() = ruta.getOrNull(indiceActual)
        val progreso: String get() = "Pregunta ${respondidas + 1} de $META_PREGUNTAS · Dificultad $dificultadActual"
    }
}