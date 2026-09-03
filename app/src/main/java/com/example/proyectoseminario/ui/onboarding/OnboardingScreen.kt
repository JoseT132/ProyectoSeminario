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
            title = "Bienvenido a la Aventura Matemática",
            description = "Embárcate en un viaje medieval para dominar las matemáticas, desbloquear niveles y mantener tu racha."
        ),
        OnboardingPage(
            title = "Explora el mapa",
            description = "Avanza por los nodos de conocimiento. Completa los ejercicios de cada módulo para desbloquear el siguiente."
        ),
        OnboardingPage(
            title = "Ejercicios adaptativos",
            description = "Responde 20 preguntas por módulo. La dificultad se ajusta según tu desempeño y el tiempo de respuesta."
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
