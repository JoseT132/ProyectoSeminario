package com.example.proyectoseminario.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Bienvenido al Reino de las Matemáticas",
            description = "Embárcate en una aventura medieval: completa lecciones cortas, supera desafíos y derrota jefes para dominar las matemáticas."
        ),
        OnboardingPage(
            title = "Explora el mapa",
            description = "6 unidades te esperan, cada una con lecciones, un desafío aplicado 🐉 y un mini-jefe 💀. Al final te aguarda el Dragón del Caos 👑."
        ),
        OnboardingPage(
            title = "Lecciones de 5 ejercicios",
            description = "Sesiones cortas de menos de 15 minutos. Aprueba con al menos 4 de 5 aciertos y gana 10 XP la primera vez que domines cada lección."
        ),
        OnboardingPage(
            title = "Desafíos y batallas",
            description = "Pon a prueba lo aprendido con problemas del mundo real y enfréntate a los jefes: 3 vidas, tiempo por pregunta y gloria al vencer."
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = pages[currentPage].title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = pages[currentPage].description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            pages.forEachIndexed { index, _ ->
                Text(
                    text = if (index == currentPage) "●" else "○",
                    color = if (index == currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onFinish) {
                Text("Omitir")
            }

            if (currentPage == pages.size - 1) {
                Button(onClick = onFinish) {
                    Text("Comenzar")
                }
            } else {
                Button(onClick = { currentPage++ }) {
                    Text("Siguiente")
                }
            }
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val description: String
)
