package com.example.proyectoseminario.ui.mapa

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoseminario.data.local.NodoCamino

// Paleta Medieval
val ColorPergaminoFondo = Color(0xFFF4EAD5)
val ColorMaderaOscura = Color(0xFF3E2723)
val ColorOro = Color(0xFFFFB300)
val ColorHierroDesbloqueado = Color(0xFF5D4037)
val ColorHierroBloqueado = Color(0xFF757575)
val ColorVerdeVictoria = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    viewModel: MapaViewModel,
    onNodoClick: (Int) -> Unit,
    onExamenClick: () -> Unit = {}
) {
    val nodos by viewModel.nodos.collectAsState()
    val perfil by viewModel.perfil.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorMaderaOscura,
                    titleContentColor = ColorPergaminoFondo
                ),
                title = {
                    Text(
                        text = "📜 Reino de las Matemáticas",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(
                        onClick = onExamenClick,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Examen",
                            color = ColorOro,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ColorOro)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🔥 ${perfil?.rachaDias ?: 0} d  ", fontSize = 14.sp)
                        Text(
                            text = "⚔️ ${perfil?.puntos ?: 0} XP",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorMaderaOscura
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorPergaminoFondo)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabecera del Curso Actual
                item {
                    val primerNodoId = nodos.firstOrNull { it.estaDesbloqueado }?.id ?: 1
                    EncabezadoCurso(
                        tituloCurso = "Módulo I: Ecuaciones Cuadráticas",
                        onClick = { onNodoClick(primerNodoId) }
                    )
                }

                itemsIndexed(items = nodos, key = { _, nodo -> nodo.id }) { index, nodo ->
                    // Calculamos el desplazamienzo horizontal para crear el camino en serpiente/zigzag
                    val offsetX = when (index % 4) {
                        0 -> 0.dp
                        1 -> 60.dp
                        2 -> 0.dp
                        3 -> (-60).dp
                        else -> 0.dp
                    }

                    NodoMedievalItem(
                        nodo = nodo,
                        offsetX = offsetX,
                        esUltimo = index == nodos.size - 1,
                        onNodoClick = onNodoClick
                    )
                }
            }
        }
    }
}

@Composable
fun EncabezadoCurso(tituloCurso: String, onClick: () -> Unit) {
    val numero = tituloCurso.substringBefore(":")
    val subtitulo = tituloCurso.substringAfter(":").trim()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = ColorMaderaOscura,
            shadowElevation = 12.dp,
            border = BorderStroke(4.dp, ColorOro),
            modifier = Modifier
                .size(120.dp)
                .clickable(onClick = onClick)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            radius = 0.6f,
                            center = Offset(0.3f, 0.3f)
                        ),
                        shape = CircleShape
                    )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = numero,
                        color = ColorOro,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ColorOro,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = ColorMaderaOscura,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 4.dp
        ) {
            Text(
                text = subtitulo,
                color = ColorOro,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun NodoMedievalItem(
    nodo: NodoCamino,
    offsetX: androidx.compose.ui.unit.Dp,
    esUltimo: Boolean,
    onNodoClick: (Int) -> Unit
) {
    val backgroundColor = when {
        nodo.estaCompletado -> ColorVerdeVictoria
        nodo.estaDesbloqueado -> ColorHierroDesbloqueado
        else -> ColorHierroBloqueado
    }

    val borderColor = when {
        nodo.estaCompletado -> ColorOro
        nodo.estaDesbloqueado -> ColorOro
        else -> Color.DarkGray
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = offsetX)
            .padding(vertical = 8.dp)
    ) {
        // Ficha 3D del Nivel
        Surface(
            shape = CircleShape,
            color = backgroundColor,
            shadowElevation = 10.dp,
            border = BorderStroke(4.dp, borderColor),
            modifier = Modifier
                .size(80.dp)
                .clickable(enabled = nodo.estaDesbloqueado) {
                    onNodoClick(nodo.id)
                }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            radius = 0.5f,
                            center = Offset(0.3f, 0.3f)
                        ),
                        shape = CircleShape
                    )
            ) {
                when {
                nodo.estaCompletado -> Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completado",
                    tint = ColorOro,
                    modifier = Modifier.size(36.dp)
                )
                nodo.estaDesbloqueado -> Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Disponible",
                    tint = ColorOro,
                    modifier = Modifier.size(36.dp)
                )
                else -> Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Bloqueado",
                    tint = Color.LightGray,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Etiqueta de la Misión / Nivel
        Surface(
            color = ColorMaderaOscura,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 4.dp
        ) {
            Text(
                text = nodo.titulo,
                color = ColorPergaminoFondo,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }

        // Conector de camino de piedra
        if (!esUltimo) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(30.dp)
                    .background(ColorHierroDesbloqueado, shape = RoundedCornerShape(3.dp))
            )
        }
    }
}