package com.example.proyectoseminario.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectoseminario.repository.MapaRepository

class PerfilViewModelFactory(
    private val repository: MapaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            return PerfilViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}