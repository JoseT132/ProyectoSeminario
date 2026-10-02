package com.example.proyectoseminario.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BancoEjerciciosTest {

    @Test
    fun genera37Nodos() {
        val nodos = BancoEjercicios.generarNodos()
        // 6 temas x 6 nodos (4 lecciones + aplicado + boss) + 1 jefe final
        assertEquals(37, nodos.size)
    }

    @Test
    fun cadaTemaTieneLeccionesDesafioYBoss() {
        val nodos = BancoEjercicios.generarNodos()
        (1..6).forEach { temaId ->
            val delTema = nodos.filter { it.temaId == temaId }
            assertEquals(6, delTema.size)
            assertEquals(4, delTema.count { it.tipo == BancoEjercicios.TIPO_LECCION })
            assertEquals(1, delTema.count { it.tipo == BancoEjercicios.TIPO_APLICADO })
            assertEquals(1, delTema.count { it.tipo == BancoEjercicios.TIPO_BOSS })
        }
        assertEquals(1, nodos.count { it.tipo == BancoEjercicios.TIPO_BOSS_FINAL })
    }

    @Test
    fun soloElPrimerNodoIniciaDesbloqueado() {
        val nodos = BancoEjercicios.generarNodos()
        assertTrue(nodos.first().estaDesbloqueado)
        assertEquals(1, nodos.count { it.estaDesbloqueado })
    }

    @Test
    fun generaAlMenos50EjerciciosPorTema() {
        val ejercicios = BancoEjercicios.generarEjercicios()
        val nodos = BancoEjercicios.generarNodos()
        (1..6).forEach { temaId ->
            val idsTema = nodos.filter { it.temaId == temaId }.map { it.id }
            val delTema = ejercicios.filter { it.nodoId in idsTema }
            // 4 lecciones x 8 + 8 aplicados + 10 boss = 50
            assertEquals("Tema $temaId", 50, delTema.size)
        }
    }

    @Test
    fun cadaLeccionTienePoolDe8Ejercicios() {
        val ejercicios = BancoEjercicios.generarEjercicios()
        val nodosLeccion = BancoEjercicios.generarNodos()
            .filter { it.tipo == BancoEjercicios.TIPO_LECCION }
        nodosLeccion.forEach { nodo ->
            val delNodo = ejercicios.filter { it.nodoId == nodo.id }
            assertEquals("Nodo ${nodo.id}", 8, delNodo.size)
        }
    }

    @Test
    fun bossFinalTiene12Ejercicios() {
        val ejercicios = BancoEjercicios.generarEjercicios()
        val bossFinal = BancoEjercicios.generarNodos()
            .first { it.tipo == BancoEjercicios.TIPO_BOSS_FINAL }
        assertEquals(12, ejercicios.count { it.nodoId == bossFinal.id })
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
