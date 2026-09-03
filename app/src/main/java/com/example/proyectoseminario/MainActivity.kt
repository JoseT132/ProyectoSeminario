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
import com.example.proyectoseminario.data.preferences.SessionManager
import com.example.proyectoseminario.repository.AuthRepository
import com.example.proyectoseminario.repository.MapaRepository
import com.example.proyectoseminario.ui.auth.LoginScreen
import com.example.proyectoseminario.ui.auth.LoginViewModel
import com.example.proyectoseminario.ui.auth.RecuperacionScreen
import com.example.proyectoseminario.ui.auth.RegistroScreen
import com.example.proyectoseminario.ui.auth.RegistroViewModel
import com.example.proyectoseminario.ui.ejercicio.EjercicioScreen
import com.example.proyectoseminario.ui.ejercicio.EjercicioViewModel
import com.example.proyectoseminario.ui.examen.ExamenScreen
import com.example.proyectoseminario.ui.examen.ExamenViewModel
import com.example.proyectoseminario.ui.logros.LogrosScreen
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
        val mapaRepository = MapaRepository(appDao)
        val authRepository = AuthRepository(appDao)
        val sessionManager = SessionManager(this)

        val mapaViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = MapaViewModel(mapaRepository) as T
            }
        )[MapaViewModel::class.java]

        val loginViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = LoginViewModel(authRepository, sessionManager) as T
            }
        )[LoginViewModel::class.java]

        val registroViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = RegistroViewModel(authRepository, sessionManager) as T
            }
        )[RegistroViewModel::class.java]

        setContent {
            ProyectoSeminarioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        mapaViewModel = mapaViewModel,
                        loginViewModel = loginViewModel,
                        registroViewModel = registroViewModel,
                        mapaRepository = mapaRepository,
                        sessionManager = sessionManager,
                        context = this@MainActivity
                    )
                }
            }
        }
    }
}

@Composable
private fun AppNavigation(
    mapaViewModel: MapaViewModel,
    loginViewModel: LoginViewModel,
    registroViewModel: RegistroViewModel,
    mapaRepository: MapaRepository,
    sessionManager: SessionManager,
    context: android.content.Context
) {
    val navController = rememberNavController()
    val navItems = listOf(BottomNavItem.Mapa, BottomNavItem.Logros, BottomNavItem.Perfil)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)

    val startDestination = if (isLoggedIn) BottomNavItem.Mapa.route else "login"

    val mostrarBottomBar = currentRoute in listOf(
        BottomNavItem.Mapa.route,
        BottomNavItem.Logros.route,
        BottomNavItem.Perfil.route
    )

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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(BottomNavItem.Mapa.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("registro") },
                    onNavigateToRecovery = { navController.navigate("recuperacion") }
                )
            }

            composable("registro") {
                RegistroScreen(
                    viewModel = registroViewModel,
                    onRegisterSuccess = {
                        navController.navigate(BottomNavItem.Mapa.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            composable("recuperacion") {
                RecuperacionScreen(
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            composable(BottomNavItem.Mapa.route) {
                MapaScreen(
                    viewModel = mapaViewModel,
                    onNodoClick = { nodoId ->
                        Toast.makeText(context, "Nivel $nodoId seleccionado", Toast.LENGTH_SHORT).show()
                        navController.navigate("ejercicio/$nodoId")
                    },
                    onExamenClick = { navController.navigate("examen") }
                )
            }

            composable("examen") {
                val examenViewModel: ExamenViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T = ExamenViewModel(mapaRepository) as T
                    }
                )

                ExamenScreen(
                    viewModel = examenViewModel,
                    onExamenComplete = { navController.popBackStack() }
                )
            }

            composable(
                route = "ejercicio/{nodoId}",
                arguments = listOf(navArgument("nodoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val nodoId = backStackEntry.arguments?.getInt("nodoId") ?: 1

                val ejercicioViewModel: EjercicioViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T = EjercicioViewModel(mapaRepository) as T
                    }
                )

                LaunchedEffect(nodoId) {
                    ejercicioViewModel.cargarEjercicios(nodoId)
                }

                EjercicioScreen(
                    viewModel = ejercicioViewModel,
                    onSiguienteEjercicio = {
                        mapaViewModel.finalizarNivelCorrecto(nodoId)
                        navController.popBackStack()
                    }
                )
            }

            composable(BottomNavItem.Logros.route) {
                val perfilViewModel: PerfilViewModel = viewModel(
                    factory = PerfilViewModelFactory(mapaRepository, sessionManager)
                )

                LogrosScreen(viewModel = perfilViewModel)
            }

            composable(BottomNavItem.Perfil.route) {
                val perfilViewModel: PerfilViewModel = viewModel(
                    factory = PerfilViewModelFactory(mapaRepository, sessionManager)
                )

                PerfilScreen(
                    viewModel = perfilViewModel,
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}