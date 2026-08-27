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

    private val _ejercicioActual = MutableStateFlow<Ejercicio?>(null)
    val ejercicioActual: StateFlow<Ejercicio?> = _ejercicioActual.asStateFlow()

    private val _opcionSeleccionada = MutableStateFlow<Int?>(null)
    val opcionSeleccionada: StateFlow<Int?> = _opcionSeleccionada.asStateFlow()

    private val _esCorrecto = MutableStateFlow<Boolean?>(null)
    val esCorrecto: StateFlow<Boolean?> = _esCorrecto.asStateFlow()

    fun cargarEjercicio(nodoId: Int) {
        viewModelScope.launch {
            _ejercicioActual.value = mapaRepository.obtenerEjercicioPorNodo(nodoId)
        }
    }

    fun seleccionarOpcion(index: Int) {
        _opcionSeleccionada.value = index
    }

    fun verificarRespuesta() {
        val ejercicio = _ejercicioActual.value ?: return
        val seleccion = _opcionSeleccionada.value ?: return

        // Compara el índice Int seleccionado contra el Int de respuestaCorrecta
        _esCorrecto.value = (seleccion == ejercicio.respuestaCorrecta)
    }

    fun reiniciarEstado() {
        _opcionSeleccionada.value = null
        _esCorrecto.value = null
    }
}