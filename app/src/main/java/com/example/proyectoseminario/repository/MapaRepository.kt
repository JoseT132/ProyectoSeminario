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

    fun getPerfil(): Flow<PerfilUsuario?> = appDao.getPerfil()

    fun getTotalRespuestas(): Flow<Int> = appDao.getTotalRespuestas()

    fun getTotalRespuestasCorrectas(): Flow<Int> = appDao.getTotalRespuestasCorrectas()

    suspend fun obtenerEjercicioPorNodo(nodoId: Int): Ejercicio? {
        val listaEjercicios = appDao.getEjerciciosPorNodo(nodoId).firstOrNull()
        return listaEjercicios?.firstOrNull()
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

            val perfilActual = appDao.getPerfil().firstOrNull()
            if (perfilActual != null) {
                val perfilActualizado = perfilActual.copy(puntos = perfilActual.puntos + puntosGanados)
                appDao.updatePerfil(perfilActualizado)
            }
        }
    }
}