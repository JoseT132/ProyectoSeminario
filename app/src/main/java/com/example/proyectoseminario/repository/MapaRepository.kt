package com.example.proyectoseminario.repository

import com.example.proyectoseminario.data.local.AppDao
import com.example.proyectoseminario.data.local.Ejercicio
import com.example.proyectoseminario.data.local.NodoCamino
import com.example.proyectoseminario.data.local.PerfilUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MapaRepository(private val appDao: AppDao) {

    fun getTodosLosNodos(): Flow<List<NodoCamino>> = appDao.getTodosLosNodos()

    fun getPerfil(): Flow<PerfilUsuario?> = appDao.getPerfil()

    suspend fun obtenerEjercicioPorNodo(nodoId: Int): Ejercicio? {
        return appDao.getEjerciciosPorNodo(nodoId).firstOrNull()
    }

    suspend fun completarNodoYDesbloquearSiguiente(nodoActualId: Int, puntosGanados: Int = 10) {
        val nodos = appDao.getTodosLosNodos().firstOrNull() ?: return
        val nodoActual = nodos.find { it.id == nodoActualId } ?: return

        // Validación: si el nodo ya fue completado previamente, no vuelve a sumar puntos
        val yaEstabaCompletado = nodoActual.estaCompletado

        if (!yaEstabaCompletado) {
            // 1. Marcar el nodo actual como completado
            appDao.updateNodo(nodoActual.copy(estaCompletado = true))

            // 2. Desbloquear el siguiente nodo en la ruta
            val siguienteNodo = nodos.find { it.nodoPrerrequisitoId == nodoActualId }
            siguienteNodo?.let {
                appDao.updateNodo(it.copy(estaDesbloqueado = true))
            }

            // 3. Sumar puntos solo la primera vez que se resuelve
            val perfilActual = appDao.getPerfil().firstOrNull()
            if (perfilActual != null) {
                val perfilActualizado = perfilActual.copy(puntos = perfilActual.puntos + puntosGanados)
                appDao.updatePerfil(perfilActualizado)
            } else {
                appDao.insertPerfil(
                    PerfilUsuario(
                        id = 1,
                        nombre = "Estudiante",
                        rachaDias = 1,
                        puntos = puntosGanados
                    )
                )
            }
        }
    }
}