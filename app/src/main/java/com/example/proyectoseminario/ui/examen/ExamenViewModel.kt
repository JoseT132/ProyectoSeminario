package com.example.proyectoseminario.ui.examen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.repository.MapaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExamenViewModel(
    private val repository: MapaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamenUiState())
    val uiState: StateFlow<ExamenUiState> = _uiState.asStateFlow()

    private val preguntas = generarPreguntas()

    val preguntasExamen: List<PreguntaExamen> = preguntas

    fun seleccionarRespuesta(preguntaId: Int, opcionIndex: Int) {
        val actuales = _uiState.value.respuestas.toMutableMap()
        actuales[preguntaId] = opcionIndex
        _uiState.value = _uiState.value.copy(respuestas = actuales)
    }

    fun avanzarPregunta() {
        val state = _uiState.value
        if (state.respuestas.containsKey(preguntas[state.preguntaActual].id)) {
            if (state.preguntaActual < preguntas.size - 1) {
                _uiState.value = state.copy(preguntaActual = state.preguntaActual + 1)
            } else {
                finalizarExamen()
            }
        }
    }

    private fun finalizarExamen() {
        val respuestas = _uiState.value.respuestas
        var correctas = 0

        preguntas.forEach { pregunta ->
            if (respuestas[pregunta.id] == pregunta.respuestaCorrecta) {
                correctas++
            }
        }

        val total = preguntas.size
        val porcentaje = (correctas * 100) / total
        val nivelSugerido = when {
            porcentaje >= 80 -> 3
            porcentaje >= 50 -> 2
            else -> 1
        }

        viewModelScope.launch {
            repository.actualizarNivelActual(nivelSugerido)
            repository.desbloquearNodosHasta(nivelSugerido)
        }

        _uiState.value = _uiState.value.copy(
            resultado = ResultadoExamen(
                correctas = correctas,
                total = total,
                porcentaje = porcentaje,
                nivelSugerido = nivelSugerido
            )
        )
    }

    fun cerrarResultado() {
        _uiState.value = _uiState.value.copy(resultado = null)
    }

    data class ExamenUiState(
        val preguntaActual: Int = 0,
        val respuestas: Map<Int, Int> = emptyMap(),
        val resultado: ResultadoExamen? = null
    )

    data class ResultadoExamen(
        val correctas: Int,
        val total: Int,
        val porcentaje: Int,
        val nivelSugerido: Int
    )
}

private fun generarPreguntas(): List<PreguntaExamen> {
    return listOf(
        PreguntaExamen(
            id = 1,
            enunciado = "Resuelve: 2x + 4 = 10",
            opciones = listOf("x = 2", "x = 3", "x = 4", "x = 5"),
            respuestaCorrecta = 1,
            area = "Álgebra"
        ),
        PreguntaExamen(
            id = 2,
            enunciado = "Factoriza: x² - 5x + 6",
            opciones = listOf("(x+1)(x-6)", "(x-2)(x-3)", "(x+2)(x+3)", "(x-1)(x+6)"),
            respuestaCorrecta = 1,
            area = "Álgebra"
        ),
        PreguntaExamen(
            id = 3,
            enunciado = "¿Cuál es el valor de 3² + 4²?",
            opciones = listOf("5", "12", "25", "7"),
            respuestaCorrecta = 2,
            area = "Aritmética"
        ),
        PreguntaExamen(
            id = 4,
            enunciado = "Resuelve: x² - 9 = 0",
            opciones = listOf("x = 3", "x = -3", "x = ±3", "x = 9"),
            respuestaCorrecta = 2,
            area = "Álgebra"
        ),
        PreguntaExamen(
            id = 5,
            enunciado = "¿Cuál es la derivada de f(x) = x²?",
            opciones = listOf("2x", "x²", "2", "x"),
            respuestaCorrecta = 0,
            area = "Cálculo"
        ),
        PreguntaExamen(
            id = 6,
            enunciado = "Simplifica: (2x^2)(3x^3)",
            opciones = listOf("5x^5", "6x^6", "6x^5", "5x^6"),
            respuestaCorrecta = 2,
            area = "Álgebra"
        ),
        PreguntaExamen(
            id = 7,
            enunciado = "Resuelve: 5x = 20",
            opciones = listOf("x = 4", "x = 5", "x = 15", "x = 25"),
            respuestaCorrecta = 0,
            area = "Álgebra"
        ),
        PreguntaExamen(
            id = 8,
            enunciado = "¿Cuánto es el 25% de 80?",
            opciones = listOf("20", "25", "15", "30"),
            respuestaCorrecta = 0,
            area = "Aritmética"
        ),
        PreguntaExamen(
            id = 9,
            enunciado = "Resuelve: x² + 6x + 9 = 0",
            opciones = listOf("x = 3 y x = -3", "x = -3 (doble)", "x = 3 (doble)", "sin solución"),
            respuestaCorrecta = 1,
            area = "Álgebra"
        ),
        PreguntaExamen(
            id = 10,
            enunciado = "¿Cuál es el límite de (2x + 1) cuando x tiende a 1?",
            opciones = listOf("0", "1", "2", "3"),
            respuestaCorrecta = 3,
            area = "Cálculo"
        )
    )
}
