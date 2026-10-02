package com.example.proyectoseminario.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OroClaro,
    onPrimary = MaderaOscura,
    secondary = MaderaClara,
    onSecondary = Color.White,
    tertiary = RojoBoss,
    onTertiary = Color.White,
    background = PergaminoOscuro,
    onBackground = Pergamino,
    surface = Color(0xFF38221C),
    onSurface = Pergamino,
    primaryContainer = MaderaOscura,
    onPrimaryContainer = OroClaro
)

private val LightColorScheme = lightColorScheme(
    primary = MaderaMedia,
    onPrimary = Pergamino,
    secondary = OroOscuro,
    onSecondary = Color.White,
    tertiary = VerdeVictoria,
    onTertiary = Color.White,
    background = Pergamino,
    onBackground = MaderaOscura,
    surface = Color(0xFFFBF3DF),
    onSurface = MaderaOscura,
    primaryContainer = Color(0xFFFFE0A3),
    onPrimaryContainer = MaderaOscura
)

@Composable
fun ProyectoSeminarioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
