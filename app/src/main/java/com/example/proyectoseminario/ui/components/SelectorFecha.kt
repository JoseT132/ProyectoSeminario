package com.example.proyectoseminario.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

private val ALTURA_ITEM: Dp = 42.dp
private const val ITEMS_VISIBLES = 5

/**
 * Rueda tipo "carrete": columna scrollable con snap al centro.
 * El ítem centrado queda seleccionado y resaltado.
 */
@Composable
fun RuedaNumerica(
    items: List<String>,
    indiceSeleccionado: Int,
    onSeleccion: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colorSeleccionado: Color = MaterialTheme.colorScheme.primary
) {
    val estado = rememberLazyListState(
        initialFirstVisibleItemIndex = indiceSeleccionado.coerceIn(0, items.size - 1)
    )
    val fling = rememberSnapFlingBehavior(estado)

    // Índice del ítem más cercano al centro del viewport
    val indiceCentrado by remember {
        derivedStateOf {
            val info = estado.layoutInfo
            val centro = (info.viewportStartOffset + info.viewportEndOffset) / 2
            info.visibleItemsInfo
                .minByOrNull { item -> kotlin.math.abs(item.offset + item.size / 2 - centro) }
                ?.index ?: indiceSeleccionado
        }
    }

    LaunchedEffect(estado) {
        snapshotFlow { indiceCentrado }
            .collect { onSeleccion(it) }
    }

    val alturaTotal = ALTURA_ITEM * ITEMS_VISIBLES

    Box(modifier = modifier.height(alturaTotal)) {
        LazyColumn(
            state = estado,
            flingBehavior = fling,
            contentPadding = PaddingValues(vertical = ALTURA_ITEM * (ITEMS_VISIBLES / 2)),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items) { index, texto ->
                val seleccionado = index == indiceCentrado
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ALTURA_ITEM)
                ) {
                    Text(
                        text = texto,
                        fontSize = if (seleccionado) 20.sp else 15.sp,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                        color = if (seleccionado)
                            colorSeleccionado
                        else
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.alpha(
                            if (seleccionado) 1f
                            else 0.35f
                        )
                    )
                }
            }
        }

        // Guía central (líneas superior e inferior del ítem seleccionado)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(ALTURA_ITEM)
                .border(
                    width = 1.dp,
                    color = colorSeleccionado.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}

/**
 * Selector de fecha con tres carretes: día / mes / año.
 * Emite la fecha formateada "DD/MM/AAAA" en cada cambio.
 */
@Composable
fun SelectorFecha(
    anioInicial: Int = 2008,
    onFechaChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val anioActual = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }
    val anios = remember { (anioActual downTo 1940).toList() }
    val meses = remember {
        listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
    }

    var dia by remember { mutableIntStateOf(1) }
    var mes by remember { mutableIntStateOf(1) } // 1..12
    var anio by remember { mutableIntStateOf(anioInicial) }

    fun diasDelMes(m: Int, a: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, a)
        cal.set(Calendar.MONTH, m - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    LaunchedEffect(dia, mes, anio) {
        val maxDia = diasDelMes(mes, anio)
        if (dia > maxDia) {
            dia = maxDia
            return@LaunchedEffect
        }
        onFechaChange("%02d/%02d/%04d".format(dia, mes, anio))
    }

    Row(modifier = modifier.fillMaxWidth()) {
        RuedaNumerica(
            items = (1..diasDelMes(mes, anio)).map { "%02d".format(it) },
            indiceSeleccionado = dia - 1,
            onSeleccion = { dia = it + 1 },
            modifier = Modifier.weight(1f)
        )
        RuedaNumerica(
            items = meses,
            indiceSeleccionado = mes - 1,
            onSeleccion = { mes = it + 1 },
            modifier = Modifier.weight(1f)
        )
        RuedaNumerica(
            items = anios.map { it.toString() },
            indiceSeleccionado = anios.indexOf(anio).coerceAtLeast(0),
            onSeleccion = { anio = anios[it] },
            modifier = Modifier.weight(1f)
        )
    }
}
