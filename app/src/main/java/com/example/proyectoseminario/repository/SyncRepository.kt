package com.example.proyectoseminario.repository

import com.example.proyectoseminario.data.local.AppDao
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await

/**
 * Sincronización local-first con Firestore.
 * Room sigue siendo la fuente de verdad: el juego funciona sin internet y
 * Firestore encola las escrituras hasta que vuelva la conexión.
 */
class SyncRepository(private val appDao: AppDao) {

    private val usuarios get() = Firebase.firestore.collection("usuarios")

    /**
     * Sube el progreso actual a usuarios/{firebaseUid}. No usa await() a
     * propósito: Firestore encola la escritura si no hay conexión.
     */
    suspend fun subirProgreso() {
        val perfil = appDao.getPrimerPerfil().firstOrNull() ?: return
        val uid = perfil.firebaseUid ?: return

        val nodos = appDao.getTodosLosNodos().firstOrNull() ?: emptyList()

        val datos = mapOf(
            "nombre" to perfil.nombre,
            "correo" to perfil.correo,
            "puntos" to perfil.puntos,
            "rachaDias" to perfil.rachaDias,
            "nivelActual" to perfil.nivelActual,
            "precisionGeneral" to perfil.precisionGeneral.toDouble(),
            "nodosCompletados" to nodos.filter { it.estaCompletado }.map { it.id },
            "nodosDesbloqueados" to nodos.filter { it.estaDesbloqueado }.map { it.id },
            "ultimaSync" to System.currentTimeMillis()
        )

        try {
            usuarios.document(uid).set(datos, SetOptions.merge())
        } catch (_: Exception) {
            // Firestore no inicializado u otro error: se reintenta en el próximo cambio.
        }
    }

    /**
     * Restaura el progreso guardado en la nube tras iniciar sesión.
     * Los nodos se combinan (unión) para no perder progreso local.
     * Devuelve true si había documento en la nube.
     */
    suspend fun restaurarProgreso(uid: String): Boolean {
        return try {
            val doc = usuarios.document(uid).get().await()
            if (!doc.exists()) return false

            val perfil = appDao.getPrimerPerfil().firstOrNull()
            if (perfil != null) {
                appDao.updatePerfil(
                    perfil.copy(
                        puntos = (doc.getLong("puntos") ?: 0).toInt(),
                        rachaDias = (doc.getLong("rachaDias") ?: 0).toInt(),
                        nivelActual = (doc.getLong("nivelActual") ?: 1).toInt(),
                        precisionGeneral = (doc.getDouble("precisionGeneral") ?: 0.0).toFloat()
                    )
                )
            }

            val nodos = appDao.getTodosLosNodos().firstOrNull() ?: emptyList()
            val completados = (doc.get("nodosCompletados") as? List<*>)?.filterIsInstance<Long>()
                ?.map { it.toInt() }?.toSet() ?: emptySet()
            val desbloqueados = (doc.get("nodosDesbloqueados") as? List<*>)?.filterIsInstance<Long>()
                ?.map { it.toInt() }?.toSet() ?: emptySet()

            nodos.forEach { nodo ->
                val completado = nodo.estaCompletado || completados.contains(nodo.id)
                val desbloqueado = nodo.estaDesbloqueado || desbloqueados.contains(nodo.id)
                if (completado != nodo.estaCompletado || desbloqueado != nodo.estaDesbloqueado) {
                    appDao.updateNodo(nodo.copy(
                        estaCompletado = completado,
                        estaDesbloqueado = desbloqueado
                    ))
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}
