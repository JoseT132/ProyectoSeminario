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

enum class EstadoRespuesta {
    NINGUNO,
    CORRECTO,
    INCORRECTO
}

@Composable
fun EjercicioScreen(
    ejercicio: Ejercicio?,
    cargando: Boolean,
    onSiguienteEjercicio: () -> Unit = {}
) {
    if (cargando) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (ejercicio == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No hay ejercicios disponibles para este nivel.")
        }
        return
    }

    // Convertimos las opciones individuales en una lista ordenada
    val opciones = listOf(
        ejercicio.opcionA,
        ejercicio.opcionB,
        ejercicio.opcionC,
        ejercicio.opcionD
    )

    // Obtenemos el texto de la respuesta correcta basándonos en el índice (0, 1, 2, 3)
    val textoRespuestaCorrecta = opciones.getOrElse(ejercicio.respuestaCorrecta) { "" }

    var opcionSeleccionada by remember(ejercicio.id) { mutableStateOf("") }
    var estadoRespuesta by remember(ejercicio.id) { mutableStateOf(EstadoRespuesta.NINGUNO) }
    var mostrarExplicacion by remember(ejercicio.id) { mutableStateOf(false) }

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
                    text = ejercicio.enunciado,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                opciones.forEach { opcion ->
                    OutlinedButton(
                        onClick = {
                            opcionSeleccionada = opcion
                            estadoRespuesta = EstadoRespuesta.NINGUNO
                            mostrarExplicacion = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (opcionSeleccionada == opcion)
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
                AnimatedVisibility(visible = estadoRespuesta == EstadoRespuesta.CORRECTO) {
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
                AnimatedVisibility(visible = estadoRespuesta == EstadoRespuesta.INCORRECTO) {
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
                                    text = ejercicio.explicacion,
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
                        if (estadoRespuesta == EstadoRespuesta.NINGUNO) {
                            if (opcionSeleccionada == textoRespuestaCorrecta) {
                                estadoRespuesta = EstadoRespuesta.CORRECTO
                            } else {
                                estadoRespuesta = EstadoRespuesta.INCORRECTO
                            }
                        } else {
                            estadoRespuesta = EstadoRespuesta.NINGUNO
                            opcionSeleccionada = ""
                            mostrarExplicacion = false
                            onSiguienteEjercicio()
                        }
                    },
                    enabled = opcionSeleccionada.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (estadoRespuesta == EstadoRespuesta.NINGUNO) "Comprobar" else "Continuar",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}