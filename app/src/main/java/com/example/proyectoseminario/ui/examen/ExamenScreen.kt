package com.example.proyectoseminario.ui.examen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamenScreen(
    viewModel: ExamenViewModel,
    onExamenComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pregunta = viewModel.preguntasExamen[uiState.preguntaActual]
    val esUltima = uiState.preguntaActual == viewModel.preguntasExamen.size - 1
    val seleccionActual = uiState.respuestas[pregunta.id]

    if (uiState.resultado != null) {
        ResultadoExamenDialog(
            resultado = uiState.resultado!!,
            onAceptar = {
                viewModel.cerrarResultado()
                onExamenComplete()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Examen de ubicación") },
                actions = {
                    Text(
                        text = "Pregunta ${uiState.preguntaActual + 1} de ${viewModel.preguntasExamen.size}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Responde para descubrir tu nivel inicial.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = pregunta.enunciado,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        pregunta.opciones.forEachIndexed { index, opcion ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.seleccionarRespuesta(pregunta.id, index) }
                                    .padding(vertical = 6.dp)
                            ) {
                                RadioButton(
                                    selected = seleccionActual == index,
                                    onClick = { viewModel.seleccionarRespuesta(pregunta.id, index) }
                                )
                                Text(
                                    text = opcion,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.avanzarPregunta() },
                enabled = seleccionActual != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(50.dp)
            ) {
                Text(if (esUltima) "Finalizar examen" else "Siguiente")
            }
        }
    }
}

@Composable
fun ResultadoExamenDialog(
    resultado: ExamenViewModel.ResultadoExamen,
    onAceptar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onAceptar,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Resultado del examen") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Respuestas correctas: ${resultado.correctas} de ${resultado.total}",
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Porcentaje: ${resultado.porcentaje}%")
                Text(
                    text = "Tu nivel sugerido es: Módulo ${resultado.nivelSugerido}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(onClick = onAceptar) {
                Text("Continuar")
            }
        }
    )
}
