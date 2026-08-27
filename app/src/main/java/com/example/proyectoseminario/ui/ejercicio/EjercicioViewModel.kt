package com.example.proyectoseminario.ui.ejercicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.data.local.AppDao
import com.example.proyectoseminario.data.local.Ejercicio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EjercicioViewModel(private val appDao: AppDao) : ViewModel() {

    private val _ejercicioActual = MutableStateFlow<Ejercicio?>(null)
    val ejercicioActual: StateFlow<Ejercicio?> = _ejercicioActual

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    fun cargarEjercicioPorNodo(nodoId: Int) {
        viewModelScope.launch {
            _cargando.value = true
            val lista = appDao.getEjerciciosPorNodo(nodoId)
            _ejercicioActual.value = lista.firstOrNull()
            _cargando.value = false
        }
    }
}

// Factory para instanciar el ViewModel pasando el AppDao
class EjercicioViewModelFactory(private val appDao: AppDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EjercicioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EjercicioViewModel(appDao) as T
        }
        throw IllegalArgumentException("ViewModel Class no encontrada")
    }
}