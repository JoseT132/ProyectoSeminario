package com.example.proyectoseminario.repository

import com.example.proyectoseminario.data.local.AppDao
import com.example.proyectoseminario.data.local.Ejercicio
import com.example.proyectoseminario.data.local.NodoCamino
import com.example.proyectoseminario.data.local.PerfilUsuario
import com.example.proyectoseminario.data.local.RegistroRespuesta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MapaRepository(private val appDao: AppDao) {

    fun getTodosLosNodos(): Flow<List<NodoCamino>> = appDao.getTodosLosNodos()

    fun getPerfil(): Flow<PerfilUsuario?> = appDao.getPrimerPerfil()

    fun getTotalRespuestas(): Flow<Int> = appDao.getTotalRespuestas()

    fun getTotalRespuestasCorrectas(): Flow<Int> = appDao.getTotalRespuestasCorrectas()

    suspend fun obtenerEjerciciosPorNodo(nodoId: Int): List<Ejercicio> {
        return appDao.getEjerciciosPorNodo(nodoId).firstOrNull() ?: emptyList()
    }

    suspend fun calcularDominioNodo(nodoId: Int): Int {
        val total = appDao.contarEjerciciosPorNodo(nodoId)
        val correctas = appDao.contarRespuestasCorrectasPorNodo(nodoId)
        return if (total > 0) (correctas * 100) / total else 0
    }

    suspend fun guardarRespuesta(nodoId: Int, ejercicioId: Int, esCorrecto: Boolean, tiempoSegundos: Int = 0) {
        val registro = RegistroRespuesta(
            nodoId = nodoId,
            ejercicioId = ejercicioId,
            esCorrecto = esCorrecto,
            tiempoSegundos = tiempoSegundos
        )
        appDao.insertRegistroRespuesta(registro)
    }

    suspend fun actualizarRachaDias(rachaDias: Int) {
        val perfil = appDao.getPrimerPerfil().firstOrNull() ?: return
        appDao.updatePerfil(perfil.copy(rachaDias = rachaDias))
    }

    suspend fun actualizarNivelActual(nivel: Int) {
        val perfil = appDao.getPrimerPerfil().firstOrNull() ?: return
        appDao.updatePerfil(perfil.copy(nivelActual = nivel))
    }

    suspend fun desbloquearNodosHasta(nivel: Int) {
        val nodos = appDao.getTodosLosNodos().firstOrNull() ?: return
        nodos.filter { it.nivelOrden <= nivel }.forEach { nodo ->
            if (!nodo.estaDesbloqueado) {
                appDao.updateNodo(nodo.copy(estaDesbloqueado = true))
            }
        }
    }

    suspend fun completarNodoYDesbloquearSiguiente(nodoActualId: Int, puntosGanados: Int = 10) {
        val nodos = appDao.getTodosLosNodos().firstOrNull() ?: return
        val nodoActual = nodos.find { it.id == nodoActualId } ?: return

        val yaEstabaCompletado = nodoActual.estaCompletado

        if (!yaEstabaCompletado) {
            appDao.updateNodo(nodoActual.copy(estaCompletado = true))

            val siguienteNodo = nodos.find { it.nodoPrerrequisitoId == nodoActualId }
            siguienteNodo?.let {
                appDao.updateNodo(it.copy(estaDesbloqueado = true))
            }

            val perfilActual = appDao.getPrimerPerfil().firstOrNull()
            if (perfilActual != null) {
                val perfilActualizado = perfilActual.copy(puntos = perfilActual.puntos + puntosGanados)
                appDao.updatePerfil(perfilActualizado)
            }
        }
    }
}