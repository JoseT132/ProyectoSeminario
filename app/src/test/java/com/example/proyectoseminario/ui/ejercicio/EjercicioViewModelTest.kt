package com.example.proyectoseminario.ui.ejercicio

import com.example.proyectoseminario.data.local.Ejercicio
import com.example.proyectoseminario.repository.MapaRepository
import com.example.proyectoseminario.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EjercicioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<MapaRepository>(relaxed = true)
    private lateinit var viewModel: EjercicioViewModel

    private val ejercicio = Ejercicio(
        nodoId = 1,
        enunciado = "2 + 2",
        opcionA = "3",
        opcionB = "4",
        opcionC = "5",
        opcionD = "6",
        respuestaCorrecta = 1,
        dificultad = 1,
        explicacion = "2 + 2 = 4"
    )

    @Before
    fun setUp() {
        viewModel = EjercicioViewModel(repository)
    }

    @Test
    fun `cargar ejercicios actualiza uiState`() = runTest {
        coEvery { repository.obtenerEjerciciosPorNodo(1) } returns listOf(ejercicio)

        viewModel.cargarEjercicios(1)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.ejercicios.size)
        assertEquals(1, state.nodoId)
        assertNotNull(state.ejercicioActual)
        assertEquals(1, state.dificultadActual)
    }

    @Test
    fun `seleccionar y verificar respuesta correcta`() = runTest {
        coEvery { repository.obtenerEjerciciosPorNodo(1) } returns listOf(ejercicio)
        viewModel.cargarEjercicios(1)

        viewModel.seleccionarOpcion(1)
        viewModel.verificarRespuesta()

        val state = viewModel.uiState.value
        assertEquals(true, state.esCorrecto)
        coVerify { repository.guardarRespuesta(1, any(), true, any()) }
    }

    @Test
    fun `siguiente avanza solo si la respuesta es correcta`() = runTest {
        coEvery { repository.obtenerEjerciciosPorNodo(1) } returns listOf(
            ejercicio,
            ejercicio.copy(id = 2, dificultad = 2, respuestaCorrecta = 0)
        )
        viewModel.cargarEjercicios(1)

        viewModel.seleccionarOpcion(1)
        viewModel.verificarRespuesta()
        viewModel.siguiente()

        val state = viewModel.uiState.value
        assertEquals(1, state.aciertos)
        assertEquals(2, state.dificultadActual)
    }

    @Test
    fun `respuesta incorrecta no suma aciertos`() = runTest {
        coEvery { repository.obtenerEjerciciosPorNodo(1) } returns listOf(ejercicio)
        viewModel.cargarEjercicios(1)

        viewModel.seleccionarOpcion(0)
        viewModel.verificarRespuesta()
        viewModel.siguiente()

        val state = viewModel.uiState.value
        assertEquals(0, state.aciertos)
        assertEquals(1, state.dificultadActual)
    }

    @Test
    fun `reiniciar vuelve al primer ejercicio`() = runTest {
        coEvery { repository.obtenerEjerciciosPorNodo(1) } returns listOf(ejercicio)
        viewModel.cargarEjercicios(1)
        viewModel.seleccionarOpcion(1)
        viewModel.verificarRespuesta()
        viewModel.siguiente()

        viewModel.reiniciar()

        val state = viewModel.uiState.value
        assertEquals(0, state.aciertos)
        assertEquals(0, state.respondidas)
        assertNull(state.esCorrecto)
    }
}
