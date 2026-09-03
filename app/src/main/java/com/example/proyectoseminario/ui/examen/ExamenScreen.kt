package com.example.proyectoseminario.ui.examen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
                title = { Text("Examen de ubicación") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Responde las 10 preguntas para descubrir tu nivel inicial.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.preguntasExamen, key = { it.id }) { pregunta ->
                    PreguntaCard(
                        pregunta = pregunta,
                        seleccionada = uiState.respuestas[pregunta.id],
                        onSeleccionar = { index ->
                            viewModel.seleccionarRespuesta(pregunta.id, index)
                        }
                    )
                }
            }

            Button(
                onClick = { viewModel.finalizarExamen() },
                enabled = uiState.respuestas.size == viewModel.preguntasExamen.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(50.dp)
            ) {
                Text("Finalizar examen")
            }
        }
    }
}

@Composable
fun PreguntaCard(
    pregunta: PreguntaExamen,
    seleccionada: Int?,
    onSeleccionar: (Int) -> Unit
) {
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
                text = "${pregunta.id}. ${pregunta.enunciado}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            pregunta.opciones.forEachIndexed { index, opcion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeleccionar(index) }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = seleccionada == index,
                        onClick = { onSeleccionar(index) }
                    )
                    Text(
                        text = opcion,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
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
