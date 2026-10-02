package com.example.proyectoseminario.ui.boss

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoseminario.ui.components.EmptyContent
import com.example.proyectoseminario.ui.components.LoadingContent

private val ColorBossFondo = Color(0xFF2B1B17)
private val ColorBossAcento = Color(0xFFD84315)
private val ColorVida = Color(0xFFE53935)

@Composable
fun BossScreen(
    viewModel: BossViewModel,
    onSalir: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingContent()
        return
    }

    if (state.ejercicios.isEmpty()) {
        EmptyContent(message = "Este jefe aún no tiene desafíos.")
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBossFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Banner del jefe
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ColorBossAcento),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚔️ ${state.nombreBoss} ⚔️",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.progresoTexto,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Vidas y temporizador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        repeat(BossViewModel.VIDAS_INICIALES) { i ->
                            Text(
                                text = if (i < state.vidas) "❤️" else "🖤",
                                fontSize = 22.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = "⏱ ${state.tiempoRestante}s",
                        color = if (state.tiempoRestante <= 10) ColorVida else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val tiempoAnimado by animateFloatAsState(
                    targetValue = state.progresoTiempo,
                    label = "temporizador"
                )
                LinearProgressIndicator(
                    progress = { tiempoAnimado },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (state.tiempoRestante <= 10) ColorVida else Color(0xFFFFB300),
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                val ejercicio = state.ejercicioActual
                if (ejercicio != null) {
                    AnimatedContent(
                        targetState = ejercicio,
                        transitionSpec = {
                            (slideInHorizontally { it } + fadeIn())
                                .togetherWith(slideOutHorizontally { -it } + fadeOut())
                        },
                        label = "preguntaBoss"
                    ) { ej ->
                        Column {
                            Text(
                                text = ej.enunciado,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )

                            listOf(ej.opcionA, ej.opcionB, ej.opcionC, ej.opcionD)
                                .forEachIndexed { index, opcion ->
                                    val containerColor = when {
                                        state.esCorrecto != null && index == ej.respuestaCorrecta -> Color(0xFFC8E6C9)
                                        state.esCorrecto == false && index == state.opcionSeleccionada -> Color(0xFFFFCDD2)
                                        state.opcionSeleccionada == index -> Color(0xFF6D4C41)
                                        else -> Color(0xFF3E2723)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.seleccionarOpcion(index) },
                                        enabled = state.esCorrecto == null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = containerColor,
                                            contentColor = if (containerColor == Color(0xFF3E2723) || containerColor == Color(0xFF6D4C41))
                                                Color.White
                                            else
                                                Color(0xFF1B1B1B)
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF8D6E63))
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
                    }
                }
            }

            // Feedback y acción
            Column {
                AnimatedVisibility(visible = state.esCorrecto == true) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "¡Golpe certero! Aciertos: ${state.aciertos}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = state.esCorrecto == false) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = ColorVida),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "¡El jefe te golpeó! -1 vida",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (state.esCorrecto == null) viewModel.verificarRespuesta()
                        else viewModel.siguientePregunta()
                    },
                    enabled = state.esCorrecto == null && state.opcionSeleccionada != null
                            || state.esCorrecto != null,
                    colors = ButtonDefaults.buttonColors(containerColor = ColorBossAcento),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (state.esCorrecto == null) "Atacar" else "Continuar",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Overlay de victoria
        if (state.fase == BossViewModel.FaseBoss.VICTORIA) {
            BossResultadoOverlay(
                titulo = "¡VICTORIA!",
                emoji = "🏆",
                mensaje = "Derrotaste a ${state.nombreBoss} con ${state.vidas} vida(s) restantes.",
                textoBoton = "Reclamar gloria",
                color = Color(0xFF4CAF50),
                onClick = onSalir
            )
        }

        // Overlay de derrota
        if (state.fase == BossViewModel.FaseBoss.DERROTA) {
            BossResultadoOverlay(
                titulo = "DERROTA",
                emoji = "💀",
                mensaje = "${state.nombreBoss} te venció. Entrena y vuelve a intentarlo.",
                textoBoton = "Reintentar",
                color = ColorVida,
                onClick = { viewModel.reiniciar() },
                textoSecundario = "Huir",
                onSecundario = onSalir
            )
        }
    }
}

@Composable
private fun BossResultadoOverlay(
    titulo: String,
    emoji: String,
    mensaje: String,
    textoBoton: String,
    color: Color,
    onClick: () -> Unit,
    textoSecundario: String? = null,
    onSecundario: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn() + fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3E2723)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = emoji, fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = titulo,
                        color = color,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mensaje,
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = color),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(textoBoton, color = Color.White)
                    }
                    if (textoSecundario != null && onSecundario != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onSecundario) {
                            Text(textoSecundario, color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}
