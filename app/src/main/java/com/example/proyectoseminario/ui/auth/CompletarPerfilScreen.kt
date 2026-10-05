package com.example.proyectoseminario.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyectoseminario.data.local.PerfilUsuario
import com.example.proyectoseminario.ui.components.SelectorFecha
import com.example.proyectoseminario.ui.components.SelectorNivelEscolar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Paso post-login obligatorio: fecha de nacimiento y nivel escolar.
 * Solo aparece para perfiles incompletos (típicamente cuentas Google);
 * sin ambos datos la cuenta no queda activa.
 */
@Composable
fun CompletarPerfilScreen(
    perfilFlow: Flow<PerfilUsuario?>,
    onGuardar: suspend (String, String) -> Unit,
    onContinuar: () -> Unit
) {
    val perfil by perfilFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    var fechaSeleccionada by remember { mutableStateOf("") }
    var nivelSeleccionado by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }
    var continuado by remember { mutableStateOf(false) }

    // Evita navegar dos veces: el botón y el LaunchedEffect pueden disparar juntos.
    val continuarUnaVez: () -> Unit = {
        if (!continuado) {
            continuado = true
            onContinuar()
        }
    }

    // Si el perfil ya está completo, no hay nada que pedir: ir directo al mapa.
    LaunchedEffect(perfil?.fechaNacimiento, perfil?.nivelEscolar) {
        val p = perfil ?: return@LaunchedEffect
        if (p.fechaNacimiento.isNotBlank() && p.nivelEscolar.isNotBlank()) {
            continuarUnaVez()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Un último paso!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "¿Cuándo naciste? Nos ayuda a adaptar las lecciones a tu edad.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SelectorFecha(
            onFechaChange = { fechaSeleccionada = it },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Seleccionada: ${fechaSeleccionada.ifBlank { "—" }}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Text(
            text = "¿Cuál es tu nivel de escolaridad?",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SelectorNivelEscolar(
            onNivelChange = { nivelSeleccionado = it },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Nivel: ${nivelSeleccionado.ifBlank { "—" }}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    guardando = true
                    onGuardar(fechaSeleccionada, nivelSeleccionado)
                    guardando = false
                    continuarUnaVez()
                }
            },
            enabled = !guardando && fechaSeleccionada.isNotBlank()
                    && nivelSeleccionado.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (guardando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Guardar y continuar")
            }
        }

    }
}
