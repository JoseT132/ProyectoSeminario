package com.example.proyectoseminario.repository

import com.example.proyectoseminario.data.local.AppDao
import com.example.proyectoseminario.data.local.NodoCamino
import com.example.proyectoseminario.data.local.PerfilUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MapaRepository(private val appDao: AppDao) {

    fun getTodosLosNodos(): Flow<List<NodoCamino>> = appDao.getTodosLosNodos()

    fun getPerfil(): Flow<PerfilUsuario?> = appDao.getPerfil()

    suspend fun completarNodoYDesbloquearSiguiente(nodoActualId: Int, puntosGanados: Int = 10) {
        // 1. Obtener la lista actual de nodos
        val nodos = appDao.getTodosLosNodos().firstOrNull() ?: return

        // 2. Marcar el nodo actual como completado
        val nodoActual = nodos.find { it.id == nodoActualId }
        nodoActual?.let {
            appDao.updateNodo(it.copy(estaCompletado = true))
        }

        // 3. Desbloquear el siguiente nodo en la ruta (si existe)
        val siguienteNodo = nodos.find { it.nodoPrerrequisitoId == nodoActualId }
        siguienteNodo?.let {
            appDao.updateNodo(it.copy(estaDesbloqueado = true))
        }

        // 4. Actualizar puntaje del perfil
        val perfilActual = appDao.getPerfil().firstOrNull()
        if (perfilActual != null) {
            val perfilActualizado = perfilActual.copy(puntos = perfilActual.puntos + puntosGanados)
            appDao.updatePerfil(perfilActualizado)
        } else {
            // Si no existe perfil inicial, lo creamos con los puntos
            appDao.insertPerfil(PerfilUsuario(id = 1, nombre = "Estudiante", rachaDias = 1, puntos = puntosGanados))
        }
    }
}