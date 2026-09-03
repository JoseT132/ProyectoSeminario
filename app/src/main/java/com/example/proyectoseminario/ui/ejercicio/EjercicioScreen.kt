package com.example.proyectoseminario.ui.ejercicio

import androidx.compose.animation.AnimatedVisibility
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

@Composable
fun EjercicioScreen(
    viewModel: EjercicioViewModel,
    onSiguienteEjercicio: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val ejercicioActual = state.ejercicioActual
    var mostrarExplicacion by remember { mutableStateOf(false) }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (ejercicioActual == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No hay ejercicios disponibles para este nivel.")
        }
        return
    }

    val opciones = listOf(
        ejercicioActual.opcionA,
        ejercicioActual.opcionB,
        ejercicioActual.opcionC,
        ejercicioActual.opcionD
    )

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
                    text = state.progreso,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = ejercicioActual.enunciado,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                opciones.forEachIndexed { index, opcion ->
                    val containerColor = when {
                        state.esCorrecto != null && index == ejercicioActual.respuestaCorrecta -> Color(0xFFC8E6C9)
                        state.esCorrecto == false && index == state.opcionSeleccionada -> Color(0xFFFFCDD2)
                        state.opcionSeleccionada == index -> MaterialTheme.colorScheme.primaryContainer
                        else -> Color.Transparent
                    }

                    val contentColor = when {
                        state.esCorrecto != null && index == ejercicioActual.respuestaCorrecta -> Color(0xFF1B5E20)
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

            Column {
                AnimatedVisibility(visible = state.esCorrecto == true) {
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
                                text = "Has dominado este ejercicio.",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = state.esCorrecto == false) {
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
                                text = "Sigue practicando para alcanzar el 80% de dominio.",
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

                            if (mostrarExplicacion) {
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
                                    "¡Módulo dominado!"
                                else
                                    "Dominio insuficiente",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = if (state.dominioAlcanzado)
                                    "Alcanzaste al menos el 80% correcto. Puedes continuar."
                                else
                                    "Necesitas al menos 80% correcto para desbloquear el siguiente módulo.",
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
                            state.esCorrecto == null -> "Comprobar"
                            state.finalizado && state.dominioAlcanzado -> "Continuar"
                            state.finalizado && !state.dominioAlcanzado -> "Repetir módulo"
                            else -> "Siguiente"
                        },
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}