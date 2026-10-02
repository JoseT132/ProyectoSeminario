package com.example.proyectoseminario.ui.desafios

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import com.example.proyectoseminario.data.local.BancoEjercicios
import com.example.proyectoseminario.data.local.NodoCamino

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesafiosScreen(
    viewModel: DesafiosViewModel,
    onDesafioClick: (NodoCamino) -> Unit
) {
    val desafios by viewModel.desafios.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Desafíos", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    text = "Pon a prueba lo aprendido con problemas del mundo real y enfréntate a los jefes de cada tema.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(desafios, key = { it.id }) { nodo ->
                DesafioCard(
                    nodo = nodo,
                    onClick = { if (nodo.estaDesbloqueado) onDesafioClick(nodo) }
                )
            }
        }
    }
}

@Composable
private fun DesafioCard(
    nodo: NodoCamino,
    onClick: () -> Unit
) {
    val emoji = when (nodo.tipo) {
        BancoEjercicios.TIPO_APLICADO -> "🐉"
        BancoEjercicios.TIPO_BOSS -> "💀"
        else -> "👑"
    }

    val etiquetaTipo = when (nodo.tipo) {
        BancoEjercicios.TIPO_APLICADO -> "Problemas aplicados"
        BancoEjercicios.TIPO_BOSS -> "Mini-jefe"
        else -> "Jefe final"
    }

    val estadoTexto = when {
        nodo.estaCompletado -> "Completado"
        nodo.estaDesbloqueado -> "Disponible"
        else -> "Bloqueado"
    }

    val estadoColor by animateColorAsState(
        targetValue = when {
            nodo.estaCompletado -> Color(0xFF2E7D32)
            nodo.estaDesbloqueado -> Color(0xFFFFB300)
            else -> Color(0xFF757575)
        },
        label = "estadoColor"
    )

    Card(
        onClick = onClick,
        enabled = nodo.estaDesbloqueado,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (nodo.estaDesbloqueado) Color(0xFF3E2723) else Color(0xFFBDBDBD)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nodo.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = etiquetaTipo,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (nodo.descripcion.isNotBlank()) {
                    Text(
                        text = nodo.descripcion,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Surface(
                color = estadoColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = estadoTexto,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
