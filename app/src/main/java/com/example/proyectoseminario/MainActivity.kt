package com.example.proyectoseminario

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectoseminario.data.local.AppDatabase
import com.example.proyectoseminario.repository.MapaRepository
import com.example.proyectoseminario.ui.ejercicio.EjercicioScreen
import com.example.proyectoseminario.ui.ejercicio.EjercicioViewModel
import com.example.proyectoseminario.ui.mapa.MapaScreen
import com.example.proyectoseminario.ui.mapa.MapaViewModel
import com.example.proyectoseminario.ui.navigation.BottomNavItem
import com.example.proyectoseminario.ui.perfil.PerfilScreen
import com.example.proyectoseminario.ui.perfil.PerfilViewModel
import com.example.proyectoseminario.ui.perfil.PerfilViewModelFactory
import com.example.proyectoseminario.ui.theme.ProyectoSeminarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this, lifecycleScope)
        val appDao = database.appDao()
        val repository = MapaRepository(appDao)

        val mapaViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MapaViewModel(repository) as T
            }
        }
        val mapaViewModel = ViewModelProvider(this, mapaViewModelFactory)[MapaViewModel::class.java]

        setContent {
            ProyectoSeminarioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navItems = listOf(BottomNavItem.Mapa, BottomNavItem.Perfil)
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // Ocultar la barra inferior si estamos resolviendo un ejercicio
                    val mostrarBottomBar = currentRoute in listOf(BottomNavItem.Mapa.route, BottomNavItem.Perfil.route)

                    Scaffold(
                        bottomBar = {
                            if (mostrarBottomBar) {
                                NavigationBar {
                                    navItems.forEach { item ->
                                        NavigationBarItem(
                                            icon = { Icon(item.icon, contentDescription = item.title) },
                                            label = { Text(item.title) },
                                            selected = currentRoute == item.route,
                                            onClick = {
                                                if (currentRoute != item.route) {
                                                    navController.navigate(item.route) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = BottomNavItem.Mapa.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(BottomNavItem.Mapa.route) {
                                MapaScreen(
                                    viewModel = mapaViewModel,
                                    onNodoClick = { nodoId ->
                                        Toast.makeText(this@MainActivity, "Nivel $nodoId seleccionado", Toast.LENGTH_SHORT).show()
                                        navController.navigate("ejercicio/$nodoId")
                                    }
                                )
                            }

                            composable(
                                route = "ejercicio/{nodoId}",
                                arguments = listOf(navArgument("nodoId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val nodoId = backStackEntry.arguments?.getInt("nodoId") ?: 1

                                val ejercicioViewModelFactory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        @Suppress("UNCHECKED_CAST")
                                        return EjercicioViewModel(repository) as T
                                    }
                                }

                                val ejercicioViewModel: EjercicioViewModel = viewModel(
                                    factory = ejercicioViewModelFactory
                                )

                                val ejercicio by ejercicioViewModel.ejercicioActual.collectAsState()

                                LaunchedEffect(nodoId) {
                                    ejercicioViewModel.cargarEjercicio(nodoId)
                                }

                                EjercicioScreen(
                                    viewModel = ejercicioViewModel,
                                    cargando = (ejercicio == null),
                                    onSiguienteEjercicio = {
                                        mapaViewModel.finalizarNivelCorrecto(nodoId)
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable(BottomNavItem.Perfil.route) {
                                val perfilViewModel: PerfilViewModel = viewModel(
                                    factory = PerfilViewModelFactory(repository)
                                )

                                PerfilScreen(
                                    viewModel = perfilViewModel,
                                    onVolver = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}