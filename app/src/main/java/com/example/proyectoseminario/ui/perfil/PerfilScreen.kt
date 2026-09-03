package com.example.proyectoseminario.ui.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cabecera del Perfil (Nombre y Puntos)
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar de Usuario",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = uiState.perfil?.nombre ?: "Estudiante",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Correo: ${uiState.perfil?.correo ?: ""}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Nacimiento: ${uiState.perfil?.fechaNacimiento ?: "—"}  •  Nivel: ${uiState.perfil?.nivelEscolar ?: "—"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Puntos: ${uiState.perfil?.puntos ?: 0} XP",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Racha actual: ${uiState.perfil?.rachaDias ?: 0} días",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Progreso general del camino
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Progreso del Camino",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val progreso = if (uiState.totalNodos > 0) {
                            uiState.nodosCompletados.toFloat() / uiState.totalNodos.toFloat()
                        } else 0f

                        LinearProgressIndicator(
                            progress = { progreso },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                        Text(
                            text = "${uiState.nodosCompletados} de ${uiState.totalNodos} temas completados",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Resumen de Estadísticas
                Text(
                    text = "Estadísticas Generales",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TarjetaMetrica(
                        titulo = "Respondidas",
                        valor = "${uiState.totalRespuestas}",
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaMetrica(
                        titulo = "Correctas",
                        valor = "${uiState.totalCorrectas}",
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaMetrica(
                        titulo = "Precisión",
                        valor = "${uiState.precisionPorcentaje}%",
                        modifier = Modifier.weight(1f)
                    )
                }

                    Button(
                    onClick = { viewModel.cerrarSesion(onLogout) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}

@Composable
fun TarjetaMetrica(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

