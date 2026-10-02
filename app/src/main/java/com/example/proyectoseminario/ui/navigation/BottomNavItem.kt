package com.example.proyectoseminario.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val emoji: String? = null
) {
    object Mapa : BottomNavItem("mapa", "Mapa", icon = Icons.Default.Home)
    object Desafios : BottomNavItem("desafios", "Desafíos", emoji = "🐉")
    object Logros : BottomNavItem("logros", "Logros", icon = Icons.Default.Star)
    object Perfil : BottomNavItem("perfil", "Perfil", icon = Icons.Default.Person)
    object Ajustes : BottomNavItem("ajustes", "Ajustes", icon = Icons.Default.Settings)
}
