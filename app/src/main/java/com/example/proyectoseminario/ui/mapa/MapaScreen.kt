package com.example.proyectoseminario.ui.mapa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoseminario.data.local.NodoCamino

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    viewModel: MapaViewModel,
    onNodoClick: (Int) -> Unit
) {
    val nodos by viewModel.nodos.collectAsState()
    val perfil by viewModel.perfil.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ruta Adaptativa",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🔥 ${perfil?.rachaDias ?: 0} d  ", fontSize = 14.sp)
                        Text(
                            text = "⭐ ${perfil?.puntos ?: 0} pts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Especificamos el tipo explícito NodoCamino para corregir la inferencia del tipo
            items(items = nodos, key = { nodo: NodoCamino -> nodo.id }) { nodo: NodoCamino ->
                NodoItem(
                    nodo = nodo,
                    onNodoClick = onNodoClick
                )
            }
        }
    }
}

@Composable
fun NodoItem(
    nodo: NodoCamino,
    onNodoClick: (Int) -> Unit
) {
    val backgroundColor = when {
        nodo.estaCompletado -> Color(0xFF4CAF50) // Verde
        nodo.estaDesbloqueado -> Color(0xFF2196F3) // Azul
        else -> Color.LightGray
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable(enabled = nodo.estaDesbloqueado) {
                    onNodoClick(nodo.id)
                }
        ) {
            Text(
                text = "${nodo.nivelOrden}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = nodo.titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = nodo.areaMatematica,
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp)
                .background(Color.LightGray)
        )
    }
}