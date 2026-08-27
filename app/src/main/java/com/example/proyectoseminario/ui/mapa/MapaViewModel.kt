package com.example.proyectoseminario.ui.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.data.local.NodoCamino
import com.example.proyectoseminario.data.local.PerfilUsuario
import com.example.proyectoseminario.repository.MapaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(private val repository: MapaRepository) : ViewModel() {

    val nodos: StateFlow<List<NodoCamino>> = repository.getTodosLosNodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val perfil: StateFlow<PerfilUsuario?> = repository.getPerfil()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun finalizarNivelCorrecto(nodoId: Int) {
        viewModelScope.launch {
            repository.completarNodoYDesbloquearSiguiente(nodoId)
        }
    }
}