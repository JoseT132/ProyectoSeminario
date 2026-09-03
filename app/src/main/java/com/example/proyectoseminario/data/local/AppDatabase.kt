package com.example.proyectoseminario.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.firstOrNull

@Database(
    entities = [
        PerfilUsuario::class,
        NodoCamino::class,
        Ejercicio::class,
        RegistroRespuesta::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "proyecto_seminario_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun poblarBaseDeDatos() {
        val dao = appDao()
        val nodos = BancoEjercicios.generarNodos()
        val ejercicios = BancoEjercicios.generarEjercicios()

        if (dao.getTodosLosNodos().firstOrNull().isNullOrEmpty()) {
            dao.insertNodos(nodos)
            dao.insertEjercicios(ejercicios)
        }
    }
}
