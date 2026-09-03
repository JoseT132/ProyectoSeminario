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
    version = 4,
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
                    id = 102,
                    nodoId = 1,
                    enunciado = "Si x - 5 = 2, ¿cuánto vale x?",
                    opcionA = "x = 5",
                    opcionB = "x = 7",
                    opcionC = "x = 3",
                    opcionD = "x = -7",
                    respuestaCorrecta = 1,
                    dificultad = 1,
                    explicacion = "Sumamos 5 a ambos lados: x = 2 + 5 = 7."
                ),
                Ejercicio(
                    id = 103,
                    nodoId = 1,
                    enunciado = "Simplifica la expresión 3x + 2x.",
                    opcionA = "5x",
                    opcionB = "6x",
                    opcionC = "5x²",
                    opcionD = "x",
                    respuestaCorrecta = 0,
                    dificultad = 1,
                    explicacion = "Sumamos los coeficientes de términos semejantes: 3 + 2 = 5."
                ),
                Ejercicio(
                    id = 104,
                    nodoId = 1,
                    enunciado = "Resuelve: 4x = 12",
                    opcionA = "x = 4",
                    opcionB = "x = 3",
                    opcionC = "x = 12",
                    opcionD = "x = 48",
                    respuestaCorrecta = 1,
                    dificultad = 1,
                    explicacion = "Dividimos ambos lados entre 4: x = 12 / 4 = 3."
                ),
                Ejercicio(
                    id = 105,
                    nodoId = 1,
                    enunciado = "Si 2x + 3 = 11, ¿cuál es el valor de x?",
                    opcionA = "x = 7",
                    opcionB = "x = 4",
                    opcionC = "x = 5",
                    opcionD = "x = 2",
                    respuestaCorrecta = 1,
                    dificultad = 1,
                    explicacion = "Restamos 3 y dividimos entre 2: x = (11 - 3) / 2 = 4."
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
                ),
                Ejercicio(
                    id = 202,
                    nodoId = 2,
                    enunciado = "Resuelve usando la fórmula general: x² - 4 = 0",
                    opcionA = "x = 2",
                    opcionB = "x = -2",
                    opcionC = "x = ±2",
                    opcionD = "x = ±4",
                    respuestaCorrecta = 2,
                    dificultad = 2,
                    explicacion = "x² = 4, por lo que x puede ser 2 o -2."
                ),
                Ejercicio(
                    id = 203,
                    nodoId = 2,
                    enunciado = "Factoriza: x² + 7x + 12",
                    opcionA = "(x + 3)(x + 4)",
                    opcionB = "(x - 3)(x - 4)",
                    opcionC = "(x + 1)(x + 12)",
                    opcionD = "(x + 6)(x + 6)",
                    respuestaCorrecta = 0,
                    dificultad = 2,
                    explicacion = "Buscamos dos números que sumen 7 y multipliquen 12: 3 y 4."
                ),
                Ejercicio(
                    id = 204,
                    nodoId = 2,
                    enunciado = "¿Cuál es el discriminante de x² - 6x + 9 = 0?",
                    opcionA = "0",
                    opcionB = "9",
                    opcionC = "36",
                    opcionD = "-27",
                    respuestaCorrecta = 0,
                    dificultad = 2,
                    explicacion = "b² - 4ac = (-6)² - 4(1)(9) = 36 - 36 = 0."
                ),
                Ejercicio(
                    id = 205,
                    nodoId = 2,
                    enunciado = "Resuelve: x² - 7x + 10 = 0",
                    opcionA = "x = 1, x = 10",
                    opcionB = "x = 5, x = 2",
                    opcionC = "x = -5, x = -2",
                    opcionD = "x = 7, x = 0",
                    respuestaCorrecta = 1,
                    dificultad = 2,
                    explicacion = "Factorizamos (x - 5)(x - 2) = 0, por lo que x = 5 o x = 2."
                ),
                Ejercicio(
                    id = 301,
                    nodoId = 3,
                    enunciado = "¿Cuál es la derivada de f(x) = 3x?",
                    opcionA = "3",
                    opcionB = "3x",
                    opcionC = "0",
                    opcionD = "x²",
                    respuestaCorrecta = 0,
                    dificultad = 3,
                    explicacion = "La derivada de 3x respecto a x es 3."
                ),
                Ejercicio(
                    id = 302,
                    nodoId = 3,
                    enunciado = "Calcula el límite cuando x tiende a 2 de (x + 3).",
                    opcionA = "5",
                    opcionB = "2",
                    opcionC = "3",
                    opcionD = "0",
                    respuestaCorrecta = 0,
                    dificultad = 3,
                    explicacion = "Sustituimos x = 2: 2 + 3 = 5."
                ),
                Ejercicio(
                    id = 303,
                    nodoId = 3,
                    enunciado = "¿Cuál es la derivada de f(x) = x³?",
                    opcionA = "3x²",
                    opcionB = "x²",
                    opcionC = "3x",
                    opcionD = "x³",
                    respuestaCorrecta = 0,
                    dificultad = 3,
                    explicacion = "Usando la regla de la potencia: d/dx(x³) = 3x²."
                ),
                Ejercicio(
                    id = 304,
                    nodoId = 3,
                    enunciado = "Halla el límite cuando x tiende a 0 de (2x + 1).",
                    opcionA = "0",
                    opcionB = "1",
                    opcionC = "2",
                    opcionD = "-1",
                    respuestaCorrecta = 1,
                    dificultad = 3,
                    explicacion = "Sustituimos x = 0: 2(0) + 1 = 1."
                ),
                Ejercicio(
                    id = 305,
                    nodoId = 3,
                    enunciado = "¿Cuál es la derivada de f(x) = 5?",
                    opcionA = "5",
                    opcionB = "0",
                    opcionC = "1",
                    opcionD = "x",
                    respuestaCorrecta = 1,
                    dificultad = 3,
                    explicacion = "La derivada de una constante es 0."
                )
            )
            dao.insertEjercicios(ejercicios)

            // Nota: El perfil de usuario se crea o carga en el registro/login,
            // no durante la población inicial de la base de datos.
        }
    }
}