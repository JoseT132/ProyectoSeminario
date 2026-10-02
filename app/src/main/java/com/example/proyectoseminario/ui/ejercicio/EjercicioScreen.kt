package com.example.proyectoseminario.ui.ejercicio

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.proyectoseminario.ui.components.EmptyContent
import com.example.proyectoseminario.ui.components.LoadingContent

@Composable
fun EjercicioScreen(
    viewModel: EjercicioViewModel,
    tituloNivel: String = "Lección",
    onSiguienteEjercicio: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val ejercicioActual = state.ejercicioActual
    var mostrarExplicacion by remember { mutableStateOf(false) }

    if (state.isLoading) {
        LoadingContent()
        return
    }

    if (ejercicioActual == null && !state.finalizado) {
        EmptyContent(message = "No hay ejercicios disponibles para este nivel.")
        return
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$tituloNivel · ${state.progreso}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val progresoAnimado by animateFloatAsState(
                    targetValue = state.progresoFraccion,
                    label = "progresoLeccion"
                )
                LinearProgressIndicator(
                    progress = { progresoAnimado },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(bottom = 16.dp),
                )

                if (ejercicioActual != null) {
                    AnimatedContent(
                        targetState = ejercicioActual,
                        transitionSpec = {
                            (slideInHorizontally { it } + fadeIn())
                                .togetherWith(slideOutHorizontally { -it } + fadeOut())
                        },
                        label = "pregunta"
                    ) { ejercicio ->
                        Column {
                            Text(
                                text = ejercicio.enunciado,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            val opciones = listOf(
                                ejercicio.opcionA,
                                ejercicio.opcionB,
                                ejercicio.opcionC,
                                ejercicio.opcionD
                            )

                            opciones.forEachIndexed { index, opcion ->
                                val containerColor = when {
                                    state.esCorrecto != null && index == ejercicio.respuestaCorrecta -> Color(0xFFC8E6C9)
                                    state.esCorrecto == false && index == state.opcionSeleccionada -> Color(0xFFFFCDD2)
                                    state.opcionSeleccionada == index -> MaterialTheme.colorScheme.primaryContainer
                                    else -> Color.Transparent
                                }

                                val contentColor = when {
                                    state.esCorrecto != null && index == ejercicio.respuestaCorrecta -> Color(0xFF1B5E20)
                                    state.esCorrecto == false && index == state.opcionSeleccionada -> Color(0xFFB71C1C)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                OutlinedButton(
                                    onClick = {
                                        if (state.esCorrecto == null) {
                                            viewModel.seleccionarOpcion(index)
                                            mostrarExplicacion = false
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = containerColor,
                                        contentColor = contentColor
                                    )
                                ) {
                                    Text(
                                        text = opcion,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column {
                AnimatedVisibility(visible = state.esCorrecto == true && !state.finalizado) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "¡Correcto!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Aciertos: ${state.aciertos} de ${state.total}",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = state.esCorrecto == false && !state.finalizado) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Incorrecto",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Necesitas al menos 4 aciertos de 5 para aprobar.",
                                color = Color.White,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(
                                onClick = { mostrarExplicacion = !mostrarExplicacion }
                            ) {
                                Text(
                                    text = if (mostrarExplicacion) "Ocultar solución" else "Ver solución",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (mostrarExplicacion && ejercicioActual != null) {
                                Text(
                                    text = ejercicioActual.explicacion,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = state.finalizado) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (state.dominioAlcanzado)
                                Color(0xFF4CAF50)
                            else
                                Color(0xFFFF9800)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (state.dominioAlcanzado)
                                    "¡Lección dominada!"
                                else
                                    "Dominio insuficiente",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = if (state.dominioAlcanzado)
                                    "Lograste ${state.aciertos} de ${state.total} aciertos. ¡Sigue adelante!"
                                else
                                    "Lograste ${state.aciertos} de ${state.total}. Necesitas al menos 4 aciertos para continuar.",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        when {
                            state.esCorrecto == null -> viewModel.verificarRespuesta()
                            state.finalizado && state.dominioAlcanzado -> onSiguienteEjercicio()
                            state.finalizado && !state.dominioAlcanzado -> {
                                viewModel.reiniciar()
                                mostrarExplicacion = false
                            }
                            else -> {
                                viewModel.siguiente()
                                mostrarExplicacion = false
                            }
                        }
                    },
                    enabled = if (state.esCorrecto == null) state.opcionSeleccionada != null else true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = when {
                            state.finalizado && state.dominioAlcanzado -> "Continuar"
                            state.finalizado && !state.dominioAlcanzado -> "Repetir lección"
                            state.esCorrecto == null -> "Comprobar"
                            else -> "Siguiente"
                        },
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
