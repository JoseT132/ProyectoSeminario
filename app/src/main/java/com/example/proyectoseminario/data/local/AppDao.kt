package com.example.proyectoseminario.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Nodos
    @Query("SELECT * FROM nodos_camino ORDER BY nivelOrden ASC")
    fun getTodosLosNodos(): Flow<List<NodoCamino>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodos(nodos: List<NodoCamino>)

    @Update
    suspend fun updateNodo(nodo: NodoCamino)

    // Perfil
    @Query("SELECT * FROM perfil_usuario WHERE id = 1 LIMIT 1")
    fun getPerfil(): Flow<PerfilUsuario?>

    @Query("SELECT * FROM perfil_usuario WHERE correo = :correo LIMIT 1")
    suspend fun getPerfilPorCorreo(correo: String): PerfilUsuario?

    @Query("SELECT * FROM perfil_usuario ORDER BY id LIMIT 1")
    suspend fun getPrimerPerfil(): PerfilUsuario?

    @Query("SELECT COUNT(*) FROM perfil_usuario WHERE correo = :correo")
    suspend fun existeCorreo(correo: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerfil(perfil: PerfilUsuario)

    @Update
    suspend fun updatePerfil(perfil: PerfilUsuario)

    @Query("DELETE FROM perfil_usuario WHERE id = :id")
    suspend fun deletePerfil(id: Int)

    // Ejercicios
    @Query("SELECT * FROM ejercicios WHERE nodoId = :nodoId")
    fun getEjerciciosPorNodo(nodoId: Int): Flow<List<Ejercicio>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEjercicios(ejercicios: List<Ejercicio>)

    // Registro Respuestas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistroRespuesta(registro: RegistroRespuesta)

    @Query("SELECT COUNT(*) FROM registro_respuestas")
    fun getTotalRespuestas(): Flow<Int>

    @Query("SELECT COUNT(*) FROM registro_respuestas WHERE esCorrecto = 1")
    fun getTotalRespuestasCorrectas(): Flow<Int>

    // Sincronización Pendiente
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSincronizacionPendiente(item: SincronizacionPendiente)

    @Query("SELECT * FROM sincronizacion_pendiente WHERE correo = :correo ORDER BY fechaCreado ASC")
    suspend fun obtenerSincronizacionPendiente(correo: String): List<SincronizacionPendiente>

    @Query("DELETE FROM sincronizacion_pendiente WHERE id IN (:ids)")
    suspend fun eliminarSincronizaciones(ids: List<Int>)

    @Query("SELECT COUNT(*) FROM sincronizacion_pendiente WHERE correo = :correo")
    fun getCantidadPendientes(correo: String): Flow<Int>
}