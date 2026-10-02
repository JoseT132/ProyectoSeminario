package com.example.proyectoseminario.ui.mapa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoseminario.data.local.BancoEjercicios
import com.example.proyectoseminario.data.local.NodoCamino

// Paleta Medieval
val ColorPergaminoFondo = Color(0xFFF4EAD5)
val ColorMaderaOscura = Color(0xFF3E2723)
val ColorOro = Color(0xFFFFB300)
val ColorHierroDesbloqueado = Color(0xFF5D4037)
val ColorHierroBloqueado = Color(0xFF757575)
val ColorVerdeVictoria = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MapaScreen(
    viewModel: MapaViewModel,
    onNodoClick: (NodoCamino) -> Unit,
    onExamenClick: () -> Unit = {}
) {
    val nodos by viewModel.nodos.collectAsState()
    val perfil by viewModel.perfil.collectAsState()
    val progresoDominio by viewModel.progresoDominio.collectAsState()

    LaunchedEffect(nodos) {
        viewModel.cargarProgresoDominio()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorMaderaOscura,
                    titleContentColor = ColorPergaminoFondo
                ),
                title = {
                    Text(
                        text = "Reino de las Matemáticas",
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
                            .background(ColorOro, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🔥 ${perfil?.rachaDias ?: 0}  ", fontSize = 14.sp)
                        Text(
                            text = "${perfil?.puntos ?: 0} XP",
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
            val nodosPorTema = nodos.groupBy { it.temaId }
            val nodoActivoId = nodos.firstOrNull { it.estaDesbloqueado && !it.estaCompletado }?.id

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                nodosPorTema.forEach { (temaId, nodosTema) ->
                    item(key = "unidad_$temaId") {
                        val nombreTema = if (temaId == 0) "⚔️ Batalla Final ⚔️"
                            else "Unidad $temaId: ${BancoEjercicios.nombreTema(temaId)}"
                        EncabezadoUnidad(nombreTema)
                    }

                    itemsIndexed(
                        items = nodosTema,
                        key = { _, nodo -> nodo.id }
                    ) { index, nodo ->
                        val offsetX = when (index % 4) {
                            0 -> 0.dp
                            1 -> 60.dp
                            2 -> 0.dp
                            3 -> (-60).dp
                            else -> 0.dp
                        }

                        NodoEntradaAnimada {
                            NodoMedievalItem(
                                nodo = nodo,
                                offsetX = offsetX,
                                esUltimo = index == nodosTema.size - 1 &&
                                    temaId == nodosPorTema.keys.maxOrNull(),
                                esActivo = nodo.id == nodoActivoId,
                                onNodoClick = onNodoClick,
                                progresoDominio = progresoDominio[nodo.id] ?: 0,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NodoEntradaAnimada(content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }
    ) {
        content()
    }
}

@Composable
fun EncabezadoUnidad(nombreTema: String) {
    Surface(
        color = ColorMaderaOscura,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 6.dp,
        border = BorderStroke(2.dp, ColorOro),
        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
    ) {
        Text(
            text = nombreTema,
            color = ColorOro,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun NodoMedievalItem(
    nodo: NodoCamino,
    offsetX: androidx.compose.ui.unit.Dp,
    esUltimo: Boolean,
    esActivo: Boolean,
    onNodoClick: (NodoCamino) -> Unit,
    progresoDominio: Int = 0,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        nodo.estaCompletado -> ColorVerdeVictoria
        nodo.estaDesbloqueado -> ColorHierroDesbloqueado
        else -> ColorHierroBloqueado
    }

    val borderColor = when {
        nodo.estaCompletado || nodo.estaDesbloqueado -> ColorOro
        else -> Color.DarkGray
    }

    // Pulso en el nodo activo (siguiente a completar)
    val escalaPulso = if (esActivo) {
        val transicion = rememberInfiniteTransition(label = "pulso")
        transicion.animateFloat(
            initialValue = 1f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(700),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulsoNodo"
        ).value
    } else 1f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
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
                .scale(escalaPulso)
                .clickable(enabled = nodo.estaDesbloqueado) {
                    onNodoClick(nodo)
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
                    nodo.estaDesbloqueado -> when (nodo.tipo) {
                        BancoEjercicios.TIPO_APLICADO ->
                            Text("🐉", fontSize = 34.sp)
                        BancoEjercicios.TIPO_BOSS ->
                            Text("💀", fontSize = 34.sp)
                        BancoEjercicios.TIPO_BOSS_FINAL ->
                            Text("👑", fontSize = 34.sp)
                        else -> Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Disponible",
                            tint = ColorOro,
                            modifier = Modifier.size(36.dp)
                        )
                    }
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

        val progresoTexto = when {
            nodo.estaCompletado -> "100%"
            nodo.estaDesbloqueado -> "${progresoDominio}%"
            else -> "Bloqueado"
        }

        Surface(
            color = ColorMaderaOscura,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 2.dp
        ) {
            Text(
                text = progresoTexto,
                color = ColorOro,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
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
