package com.example.proyectoseminario.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BancoEjerciciosTest {

    @Test
    fun generaSeisNodos() {
        val nodos = BancoEjercicios.generarNodos()
        assertEquals(6, nodos.size)
    }

    @Test
    fun genera120Ejercicios() {
        val ejercicios = BancoEjercicios.generarEjercicios()
        assertEquals(120, ejercicios.size)
    }

    @Test
    fun genera20EjerciciosPorNodo() {
        val ejercicios = BancoEjercicios.generarEjercicios()
        (1..6).forEach { nodoId ->
            val delNodo = ejercicios.filter { it.nodoId == nodoId }
            assertEquals("Nodo $nodoId", 20, delNodo.size)
            assertEquals((1..20).toList(), delNodo.map { it.dificultad }.sorted())
        }
    }

    @Test
    fun idsSonUnicos() {
        val ejercicios = BancoEjercicios.generarEjercicios()
        assertEquals(ejercicios.size, ejercicios.map { it.id }.distinct().size)
    }

    @Test
    fun cadaEjercicioTieneCuatroOpciones() {
        val ejercicios = BancoEjercicios.generarEjercicios()
        ejercicios.forEach { ejercicio ->
            assertTrue(ejercicio.opcionA.isNotBlank())
            assertTrue(ejercicio.opcionB.isNotBlank())
            assertTrue(ejercicio.opcionC.isNotBlank())
            assertTrue(ejercicio.opcionD.isNotBlank())
            assertTrue(ejercicio.respuestaCorrecta in 0..3)
        }
    }
}
