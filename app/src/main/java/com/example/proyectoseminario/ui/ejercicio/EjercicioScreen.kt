package com.example.proyectoseminario.ui.ejercicio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoseminario.data.local.Ejercicio

@Composable
fun EjercicioScreen(
    viewModel: EjercicioViewModel,
    cargando: Boolean,
    onSiguienteEjercicio: () -> Unit = {}
) {
    val ejercicio by viewModel.ejercicioActual.collectAsState()
    val opcionSeleccionadaIndex by viewModel.opcionSeleccionada.collectAsState()
    val esCorrecto by viewModel.esCorrecto.collectAsState()

    var mostrarExplicacion by remember(ejercicio?.id) { mutableStateOf(false) }

    if (cargando) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val ejercicioActual = ejercicio
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
            // --- Sección Superior: Pregunta y Opciones ---
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = ejercicioActual.enunciado,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                opciones.forEachIndexed { index, opcion ->
                    OutlinedButton(
                        onClick = {
                            if (esCorrecto == null) {
                                viewModel.seleccionarOpcion(index)
                                mostrarExplicacion = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (opcionSeleccionadaIndex == index)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                Color.Transparent
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

            // --- Sección Inferior: Banners y Botón de Acción ---
            Column {
                // Banner Verde (Correcto)
                AnimatedVisibility(visible = esCorrecto == true) {
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
                                text = "¡Excelente trabajo! Has elegido la opción adecuada.",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Banner Rojo (Incorrecto con Desplegable)
                AnimatedVisibility(visible = esCorrecto == false) {
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
                                text = "La opción seleccionada no es la correcta.",
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

                // Botón Comprobar / Continuar
                Button(
                    onClick = {
                        if (esCorrecto == null) {
                            viewModel.verificarRespuesta()
                        } else {
                            val fueRespuestaCorrecta = esCorrecto == true
                            viewModel.reiniciarEstado()
                            mostrarExplicacion = false
                            if (fueRespuestaCorrecta) {
                                onSiguienteEjercicio()
                            }
                        }
                    },
                    enabled = opcionSeleccionadaIndex != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (esCorrecto == null) "Comprobar" else "Continuar",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}