package com.example.proyectoseminario.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PerfilUsuario::class,
        NodoCamino::class,
        Ejercicio::class,
        RegistroRespuesta::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "proyecto_seminario_db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    poblarBaseDeDatos(database.appDao())
                }
            }
        }

        suspend fun poblarBaseDeDatos(dao: AppDao) {
            // Nodos del camino con la firma exacta de NodoCamino
            val nodos = listOf(
                NodoCamino(
                    id = 1,
                    titulo = "Fundamentos de Álgebra",
                    descripcion = "Conceptos básicos y ecuaciones de primer grado",
                    areaMatematica = "Álgebra",
                    nivelOrden = 1,
                    estaDesbloqueado = true,
                    estaCompletado = false,
                    nodoPrerrequisitoId = null
                ),
                NodoCamino(
                    id = 2,
                    titulo = "Ecuaciones Cuadráticas",
                    descripcion = "Factorización y fórmula general",
                    areaMatematica = "Álgebra",
                    nivelOrden = 2,
                    estaDesbloqueado = false,
                    estaCompletado = false,
                    nodoPrerrequisitoId = 1
                ),
                NodoCamino(
                    id = 3,
                    titulo = "Introducción al Cálculo",
                    descripcion = "Límites y derivadas básicas",
                    areaMatematica = "Cálculo",
                    nivelOrden = 3,
                    estaDesbloqueado = false,
                    estaCompletado = false,
                    nodoPrerrequisitoId = 2
                )
            )
            dao.insertNodos(nodos)

            // Ejercicios Nivel 1 y Nivel 2
            val ejercicios = listOf(
                Ejercicio(
                    id = 101,
                    nodoId = 1,
                    enunciado = "¿Cuál es el resultado de resolver 2x + 4 = 10?",
                    opcionA = "x = 2",
                    opcionB = "x = 3",
                    opcionC = "x = 4",
                    opcionD = "x = 5",
                    respuestaCorrecta = 1,
                    dificultad = 1,
                    explicacion = "Restamos 4 de ambos lados: 2x = 6. Luego dividimos entre 2: x = 3."
                ),
                Ejercicio(
                    id = 201,
                    nodoId = 2,
                    enunciado = "¿Cuáles son las raíces de la ecuación x² - 5x + 6 = 0?",
                    opcionA = "x = 1, x = 6",
                    opcionB = "x = 2, x = 3",
                    opcionC = "x = -2, x = -3",
                    opcionD = "x = 0, x = 5",
                    respuestaCorrecta = 1,
                    dificultad = 2,
                    explicacion = "Factorizando la ecuación obtenemos (x - 2)(x - 3) = 0, por lo que las soluciones son x = 2 y x = 3."
                )
            )
            dao.insertEjercicios(ejercicios)

            // Nota: El perfil de usuario se crea o carga en el registro/login,
            // no durante la población inicial de la base de datos.
        }
    }
}