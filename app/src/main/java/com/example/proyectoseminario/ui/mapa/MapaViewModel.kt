package com.example.proyectoseminario.ui.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.data.local.NodoCamino
import com.example.proyectoseminario.data.local.PerfilUsuario
import com.example.proyectoseminario.repository.MapaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(private val repository: MapaRepository) : ViewModel() {

    val nodos: StateFlow<List<NodoCamino>> = repository.getTodosLosNodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val perfil: StateFlow<PerfilUsuario?> = repository.getPerfil()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _progresoDominio = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val progresoDominio: StateFlow<Map<Int, Int>> = _progresoDominio

    fun cargarProgresoDominio() {
        viewModelScope.launch {
            val ids = nodos.value.map { it.id }
            _progresoDominio.value = ids.associateWith { repository.calcularDominioNodo(it) }
        }
    }

    fun finalizarNivelCorrecto(nodoId: Int) {
        viewModelScope.launch {
            repository.completarNodoYDesbloquearSiguiente(nodoId)
        }
    }
}