package com.example.proyectoseminario.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Mapa : BottomNavItem("mapa", "Mapa", Icons.Default.Home)
    object Logros : BottomNavItem("logros", "Logros", Icons.Default.Star)
    object Perfil : BottomNavItem("perfil", "Perfil", Icons.Default.Person)
}