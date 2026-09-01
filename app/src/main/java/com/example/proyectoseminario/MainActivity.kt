package com.example.proyectoseminario

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectoseminario.data.local.AppDatabase
import com.example.proyectoseminario.repository.MapaRepository
import com.example.proyectoseminario.ui.ejercicio.EjercicioScreen
import com.example.proyectoseminario.ui.ejercicio.EjercicioViewModel
import com.example.proyectoseminario.ui.mapa.MapaScreen
import com.example.proyectoseminario.ui.mapa.MapaViewModel
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

                    NavHost(
                        navController = navController,
                        startDestination = "mapa"
                    ) {
                        composable("mapa") {
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
                    }
                }
            }
        }
    }
}