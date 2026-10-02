package com.example.proyectoseminario.ui.ejercicio

import com.example.proyectoseminario.data.local.BancoEjercicios
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
    fun `cargar ejercicios pide una sesion de 5 y actualiza uiState`() = runTest {
        coEvery {
            repository.obtenerEjerciciosSesion(1, BancoEjercicios.EJERCICIOS_POR_SESION)
        } returns listOf(ejercicio)

        viewModel.cargarEjercicios(1)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.ejercicios.size)
        assertEquals(1, state.nodoId)
        assertNotNull(state.ejercicioActual)
        coVerify { repository.obtenerEjerciciosSesion(1, 5) }
    }

    @Test
    fun `seleccionar y verificar respuesta correcta`() = runTest {
        coEvery { repository.obtenerEjerciciosSesion(any(), any()) } returns listOf(ejercicio)
        viewModel.cargarEjercicios(1)

        viewModel.seleccionarOpcion(1)
        viewModel.verificarRespuesta()

        val state = viewModel.uiState.value
        assertEquals(true, state.esCorrecto)
        assertEquals(1, state.aciertos)
        coVerify { repository.guardarRespuesta(1, any(), true, any()) }
    }

    @Test
    fun `siguiente avanza al siguiente ejercicio`() = runTest {
        coEvery { repository.obtenerEjerciciosSesion(any(), any()) } returns listOf(
            ejercicio,
            ejercicio.copy(id = 2, respuestaCorrecta = 0)
        )
        viewModel.cargarEjercicios(1)

        viewModel.seleccionarOpcion(1)
        viewModel.verificarRespuesta()
        viewModel.siguiente()

        val state = viewModel.uiState.value
        assertEquals(1, state.indiceActual)
        assertEquals(1, state.aciertos)
        assertNull(state.esCorrecto)
        assertNull(state.opcionSeleccionada)
    }

    @Test
    fun `al terminar la sesion se finaliza y domina con 4 de 5 aciertos`() = runTest {
        val cinco = (1..5).map { ejercicio.copy(id = it) }
        coEvery { repository.obtenerEjerciciosSesion(any(), any()) } returns cinco
        viewModel.cargarEjercicios(1)

        repeat(5) {
            viewModel.seleccionarOpcion(1)
            viewModel.verificarRespuesta()
            viewModel.siguiente()
        }

        val state = viewModel.uiState.value
        assertTrue(state.finalizado)
        assertTrue(state.dominioAlcanzado)
        assertEquals(5, state.aciertos)
    }

    @Test
    fun `sesion fallida con menos de 4 aciertos no domina`() = runTest {
        val cinco = (1..5).map { ejercicio.copy(id = it) }
        coEvery { repository.obtenerEjerciciosSesion(any(), any()) } returns cinco
        viewModel.cargarEjercicios(1)

        // 2 correctas, 3 incorrectas
        repeat(2) {
            viewModel.seleccionarOpcion(1)
            viewModel.verificarRespuesta()
            viewModel.siguiente()
        }
        repeat(3) {
            viewModel.seleccionarOpcion(0)
            viewModel.verificarRespuesta()
            viewModel.siguiente()
        }

        val state = viewModel.uiState.value
        assertTrue(state.finalizado)
        assertFalse(state.dominioAlcanzado)
        assertEquals(2, state.aciertos)
    }

    @Test
    fun `reiniciar vuelve al primer ejercicio con sesion nueva`() = runTest {
        coEvery { repository.obtenerEjerciciosSesion(any(), any()) } returns listOf(ejercicio)
        viewModel.cargarEjercicios(1)
        viewModel.seleccionarOpcion(1)
        viewModel.verificarRespuesta()
        viewModel.siguiente()

        viewModel.reiniciar()

        val state = viewModel.uiState.value
        assertEquals(0, state.indiceActual)
        assertEquals(0, state.aciertos)
        assertEquals(0, state.respondidas)
        assertNull(state.esCorrecto)
        assertFalse(state.finalizado)
    }
}
