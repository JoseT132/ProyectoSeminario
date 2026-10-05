package com.example.proyectoseminario.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Carrete de nivel de escolaridad: una sola rueda con snap al centro.
 * Emite el nivel seleccionado en cada cambio (y al montarse, como SelectorFecha).
 */
@Composable
fun SelectorNivelEscolar(
    onNivelChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val niveles = remember {
        listOf("Primaria", "Básico", "Diversificado", "Universidad", "Otro")
    }
    var seleccionado by remember { mutableIntStateOf(0) }

    LaunchedEffect(seleccionado) {
        onNivelChange(niveles[seleccionado])
    }

    RuedaNumerica(
        items = niveles,
        indiceSeleccionado = seleccionado,
        onSeleccion = { seleccionado = it },
        modifier = modifier.fillMaxWidth()
    )
}
