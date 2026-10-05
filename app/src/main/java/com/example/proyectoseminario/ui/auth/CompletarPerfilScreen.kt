package com.example.proyectoseminario.ui.auth

import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Paso post-login: pide la fecha de nacimiento con el carrete.
 * Si el perfil ya tiene fecha (registro manual), se salta solo al mapa.
 */
@Composable
fun CompletarPerfilScreen(
    perfilFlow: Flow<PerfilUsuario?>,
    onGuardar: suspend (String) -> Unit,
    onContinuar: () -> Unit
) {
    val perfil by perfilFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    var fechaSeleccionada by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }
    var continuado by remember { mutableStateOf(false) }

    // Evita navegar dos veces: el botón y el LaunchedEffect pueden disparar juntos.
    val continuarUnaVez: () -> Unit = {
        if (!continuado) {
            continuado = true
            onContinuar()
        }
    }

    // Si ya tiene fecha, no hay nada que completar: ir directo al mapa.
    LaunchedEffect(perfil?.fechaNacimiento) {
        if (perfil != null && perfil!!.fechaNacimiento.isNotBlank()) {
            continuarUnaVez()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
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
            modifier = Modifier.padding(bottom = 24.dp)
        )

        SelectorFecha(
            onFechaChange = { fechaSeleccionada = it },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Seleccionada: ${fechaSeleccionada.ifBlank { "—" }}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    guardando = true
                    onGuardar(fechaSeleccionada)
                    guardando = false
                    continuarUnaVez()
                }
            },
            enabled = !guardando && fechaSeleccionada.isNotBlank(),
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
