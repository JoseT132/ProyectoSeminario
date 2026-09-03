package com.example.proyectoseminario.ui.examen

import com.example.proyectoseminario.repository.MapaRepository
import com.example.proyectoseminario.utils.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ExamenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<MapaRepository>(relaxed = true)
    private lateinit var viewModel: ExamenViewModel

    @Before
    fun setUp() {
        viewModel = ExamenViewModel(repository)
    }

    @Test
    fun `inicialmente empieza en la pregunta 0 sin respuestas`() {
        val state = viewModel.uiState.value
        assertEquals(0, state.preguntaActual)
        assertTrue(state.respuestas.isEmpty())
        assertNull(state.resultado)
    }

    @Test
    fun `seleccionar respuesta y avanzar pregunta`() {
        viewModel.seleccionarRespuesta(1, 0)
        viewModel.avanzarPregunta()

        val state = viewModel.uiState.value
        assertEquals(1, state.preguntaActual)
        assertEquals(0, state.respuestas[1])
    }

    @Test
    fun `avanzar sin respuesta no cambia de pregunta`() {
        viewModel.avanzarPregunta()
        assertEquals(0, viewModel.uiState.value.preguntaActual)
    }

    @Test
    fun `finalizar examen calcula resultado y actualiza nivel`() = runTest {
        viewModel.preguntasExamen.forEachIndexed { index, pregunta ->
            viewModel.seleccionarRespuesta(pregunta.id, pregunta.respuestaCorrecta)
            viewModel.avanzarPregunta()
        }
        advanceUntilIdle()

        val resultado = viewModel.uiState.value.resultado
        assertNotNull(resultado)
        assertEquals(10, resultado?.total)
        assertEquals(10, resultado?.correctas)
        assertEquals(100, resultado?.porcentaje)
        assertEquals(3, resultado?.nivelSugerido)
        coVerify { repository.actualizarNivelActual(3) }
        coVerify { repository.desbloquearNodosHasta(3) }
    }
}
