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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerfil(perfil: PerfilUsuario)

    @Update
    suspend fun updatePerfil(perfil: PerfilUsuario)

    // Ejercicios
    @Query("SELECT * FROM ejercicios WHERE nodoId = :nodoId")
    fun getEjerciciosPorNodo(nodoId: Int): Flow<List<Ejercicio>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEjercicios(ejercicios: List<Ejercicio>)

    // Registro Respuestas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistroRespuesta(registro: RegistroRespuesta)
}