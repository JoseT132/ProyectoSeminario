package com.example.proyectoseminario.ui.desafios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.data.local.BancoEjercicios
import com.example.proyectoseminario.data.local.NodoCamino
import com.example.proyectoseminario.repository.MapaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DesafiosViewModel(
    repository: MapaRepository
) : ViewModel() {

    val desafios: StateFlow<List<NodoCamino>> = repository.getTodosLosNodos()
        .map { nodos ->
            nodos.filter {
                it.tipo == BancoEjercicios.TIPO_APLICADO ||
                    it.tipo == BancoEjercicios.TIPO_BOSS ||
                    it.tipo == BancoEjercicios.TIPO_BOSS_FINAL
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
