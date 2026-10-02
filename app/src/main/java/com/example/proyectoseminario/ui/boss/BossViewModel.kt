package com.example.proyectoseminario.ui.boss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.data.local.Ejercicio
import com.example.proyectoseminario.repository.MapaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BossViewModel(
    private val mapaRepository: MapaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BossUiState())
    val uiState: StateFlow<BossUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun cargarBoss(nodoId: Int) {
        _uiState.value = BossUiState(isLoading = true)
        viewModelScope.launch {
            val nodo = mapaRepository.obtenerNodo(nodoId)
            val ejercicios = mapaRepository.obtenerEjerciciosPorNodo(nodoId).shuffled()
            _uiState.value = BossUiState(
                isLoading = false,
                ejercicios = ejercicios,
                nodoId = nodoId,
                nombreBoss = nodo?.titulo ?: "Jefe",
                tiempoRestante = TIEMPO_POR_PREGUNTA
            )
            iniciarTimer()
        }
    }

    private fun iniciarTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.tiempoRestante > 0 &&
                _uiState.value.fase == FaseBoss.COMBATE &&
                _uiState.value.esCorrecto == null
            ) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    tiempoRestante = _uiState.value.tiempoRestante - 1
                )
            }
            if (_uiState.value.tiempoRestante <= 0 &&
                _uiState.value.fase == FaseBoss.COMBATE &&
                _uiState.value.esCorrecto == null
            ) {
                perderVida(esTiempoAgotado = true)
            }
        }
    }

    fun seleccionarOpcion(index: Int) {
        val state = _uiState.value
        if (state.esCorrecto == null && state.fase == FaseBoss.COMBATE) {
            _uiState.value = state.copy(opcionSeleccionada = index)
        }
    }

    fun verificarRespuesta() {
        val state = _uiState.value
        val ejercicio = state.ejercicioActual ?: return
        val seleccion = state.opcionSeleccionada ?: return

        val correcta = seleccion == ejercicio.respuestaCorrecta
        timerJob?.cancel()

        viewModelScope.launch {
            mapaRepository.guardarRespuesta(
                nodoId = ejercicio.nodoId,
                ejercicioId = ejercicio.id,
                esCorrecto = correcta,
                tiempoSegundos = TIEMPO_POR_PREGUNTA - state.tiempoRestante
            )
        }

        if (correcta) {
            _uiState.value = state.copy(esCorrecto = true, aciertos = state.aciertos + 1)
        } else {
            perderVida(esTiempoAgotado = false, seleccionHecha = true)
        }
    }

    private fun perderVida(esTiempoAgotado: Boolean, seleccionHecha: Boolean = false) {
        val state = _uiState.value
        val vidasRestantes = state.vidas - 1
        _uiState.value = state.copy(
            vidas = vidasRestantes,
            esCorrecto = if (seleccionHecha) false else null,
            fase = if (vidasRestantes <= 0) FaseBoss.DERROTA else FaseBoss.COMBATE
        )
        if (vidasRestantes > 0 && !seleccionHecha) {
            // Tiempo agotado: pasar a la siguiente pregunta directamente
            siguientePregunta()
        }
    }

    fun siguientePregunta() {
        val state = _uiState.value
        val siguienteIndice = state.indiceActual + 1

        if (siguienteIndice >= state.ejercicios.size) {
            victoria()
            return
        }

        _uiState.value = state.copy(
            indiceActual = siguienteIndice,
            opcionSeleccionada = null,
            esCorrecto = null,
            tiempoRestante = TIEMPO_POR_PREGUNTA
        )
        iniciarTimer()
    }

    private fun victoria() {
        val state = _uiState.value
        _uiState.value = state.copy(fase = FaseBoss.VICTORIA, esCorrecto = null)
        timerJob?.cancel()
        viewModelScope.launch {
            val puntos = if (state.ejercicios.size > 10) PUNTOS_BOSS_FINAL else PUNTOS_BOSS
            mapaRepository.completarNodoYDesbloquearSiguiente(state.nodoId, puntos)
        }
    }

    fun reiniciar() {
        timerJob?.cancel()
        cargarBoss(_uiState.value.nodoId)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    enum class FaseBoss { COMBATE, VICTORIA, DERROTA }

    companion object {
        const val TIEMPO_POR_PREGUNTA = 30
        const val VIDAS_INICIALES = 3
        const val PUNTOS_BOSS = 50
        const val PUNTOS_BOSS_FINAL = 150
    }

    data class BossUiState(
        val isLoading: Boolean = false,
        val ejercicios: List<Ejercicio> = emptyList(),
        val nodoId: Int = 0,
        val nombreBoss: String = "Jefe",
        val indiceActual: Int = 0,
        val vidas: Int = VIDAS_INICIALES,
        val tiempoRestante: Int = TIEMPO_POR_PREGUNTA,
        val opcionSeleccionada: Int? = null,
        val esCorrecto: Boolean? = null,
        val aciertos: Int = 0,
        val fase: FaseBoss = FaseBoss.COMBATE
    ) {
        val ejercicioActual: Ejercicio? get() = ejercicios.getOrNull(indiceActual)
        val total: Int get() = ejercicios.size
        val progresoTexto: String get() = "Golpe ${indiceActual + 1} de $total"
        val progresoTiempo: Float get() =
            tiempoRestante.toFloat() / TIEMPO_POR_PREGUNTA.toFloat()
    }
}
