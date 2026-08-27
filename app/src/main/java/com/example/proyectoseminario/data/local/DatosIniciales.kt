package com.example.proyectoseminario.data.local

object DatosIniciales {

    val nodosIniciales = listOf(
        NodoCamino(
            id = 1,
            titulo = "Fundamentos de Álgebra",
            descripcion = "Operaciones básicas y despeje de ecuaciones de primer grado.",
            areaMatematica = "Álgebra",
            nivelOrden = 1,
            estaDesbloqueado = true,
            estaCompletado = false
        ),
        NodoCamino(
            id = 2,
            titulo = "Ecuaciones Cuadráticas",
            descripcion = "Resolución por factorización y fórmula general.",
            areaMatematica = "Álgebra",
            nivelOrden = 2,
            estaDesbloqueado = false,
            estaCompletado = false,
            nodoPrerrequisitoId = 1
        ),
        NodoCamino(
            id = 3,
            titulo = "Introducción al Cálculo",
            descripcion = "Límites sencillos y concepto de derivada.",
            areaMatematica = "Cálculo",
            nivelOrden = 3,
            estaDesbloqueado = false,
            estaCompletado = false,
            nodoPrerrequisitoId = 2
        )
    )

    val ejerciciosIniciales = listOf(
        Ejercicio(
            nodoId = 1,
            enunciado = "Despeja x en la ecuación: 2x + 4 = 12",
            opcionA = "x = 2",
            opcionB = "x = 4",
            opcionC = "x = 6",
            opcionD = "x = 8",
            respuestaCorrecta = 1, // Opción B
            dificultad = 1,
            explicacion = "Paso 1: Resta 4 a ambos lados -> 2x = 12 - 4 -> 2x = 8.\nPaso 2: Divide entre 2 -> x = 8 / 2 -> x = 4."
        ),
        Ejercicio(
            nodoId = 1,
            enunciado = "Resuelve la expresión: 3 * (4 + 2) - 5",
            opcionA = "13",
            opcionB = "15",
            opcionC = "11",
            opcionD = "18",
            respuestaCorrecta = 0, // Opción A
            dificultad = 1,
            explicacion = "Paso 1: Resuelve el paréntesis primero -> 4 + 2 = 6.\nPaso 2: Multiplica por 3 -> 3 * 6 = 18.\nPaso 3: Resta 5 -> 18 - 5 = 13."
        )
    )
}