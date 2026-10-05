package com.example.proyectoseminario

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.proyectoseminario.data.local.BancoEjercicios
import com.example.proyectoseminario.data.local.NodoCamino
import com.example.proyectoseminario.data.preferences.SessionManager
import com.example.proyectoseminario.repository.AuthRepository
import com.example.proyectoseminario.repository.MapaRepository
import com.example.proyectoseminario.ui.ajustes.AjustesScreen
import com.example.proyectoseminario.ui.auth.LoginScreen
import com.example.proyectoseminario.ui.auth.LoginViewModel
import com.example.proyectoseminario.ui.auth.RecuperacionScreen
import com.example.proyectoseminario.ui.auth.RegistroScreen
import com.example.proyectoseminario.ui.auth.RegistroViewModel
import com.example.proyectoseminario.ui.boss.BossScreen
import com.example.proyectoseminario.ui.boss.BossViewModel
import com.example.proyectoseminario.ui.desafios.DesafiosScreen
import com.example.proyectoseminario.ui.desafios.DesafiosViewModel
import com.example.proyectoseminario.ui.ejercicio.EjercicioScreen
import com.example.proyectoseminario.ui.ejercicio.EjercicioViewModel
import com.example.proyectoseminario.ui.examen.ExamenScreen
import com.example.proyectoseminario.ui.examen.ExamenViewModel
import com.example.proyectoseminario.ui.logros.LogrosScreen
import com.example.proyectoseminario.ui.mapa.MapaScreen
import com.example.proyectoseminario.ui.mapa.MapaViewModel
import com.example.proyectoseminario.ui.navigation.BottomNavItem
import com.example.proyectoseminario.ui.onboarding.OnboardingScreen
import com.example.proyectoseminario.ui.perfil.PerfilScreen
import com.example.proyectoseminario.ui.perfil.PerfilViewModel
import com.example.proyectoseminario.ui.perfil.PerfilViewModelFactory
import com.example.proyectoseminario.ui.theme.ProyectoSeminarioTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val appDao = database.appDao()
        val mapaRepository = MapaRepository(appDao)
        val authRepository = AuthRepository(appDao)
        val sessionManager = SessionManager(this)

        lifecycleScope.launch(Dispatchers.IO) {
            database.poblarBaseDeDatos()
            if (sessionManager.isLoggedIn.firstOrNull() == true) {
                val racha = sessionManager.actualizarRacha()
                mapaRepository.actualizarRachaDias(racha)
            }
        }

        val mapaViewModel = viewModelConFactory { MapaViewModel(mapaRepository) }
        val loginViewModel = viewModelConFactory { LoginViewModel(authRepository, sessionManager) }
        val registroViewModel = viewModelConFactory { RegistroViewModel(authRepository, sessionManager) }

        setContent {
            val isSystemDark = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(isSystemDark) }
            ProyectoSeminarioTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showOnboarding by remember { mutableStateOf<Boolean?>(null) }

                    LaunchedEffect(Unit) {
                        showOnboarding = !sessionManager.hasCompletedOnboarding()
                    }

                    when (showOnboarding) {
                        null -> { Text("Cargando...") }
                        true -> OnboardingScreen(
                            onFinish = {
                                lifecycleScope.launch {
                                    sessionManager.setOnboardingCompleted()
                                    showOnboarding = false
                                }
                            }
                        )
                        false -> AppNavigation(
                            mapaViewModel = mapaViewModel,
                            loginViewModel = loginViewModel,
                            registroViewModel = registroViewModel,
                            mapaRepository = mapaRepository,
                            sessionManager = sessionManager,
                            authRepository = authRepository,
                            isDarkTheme = darkTheme,
                            onDarkThemeChange = { darkTheme = it }
                        )
                    }
                }
            }
        }
    }

    private inline fun <reified T : ViewModel> viewModelConFactory(
        crossinline factory: () -> T
    ): T = ViewModelProvider(
        this,
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
        }
    )[T::class.java]
}

@Composable
private fun AppNavigation(
    mapaViewModel: MapaViewModel,
    loginViewModel: LoginViewModel,
    registroViewModel: RegistroViewModel,
    mapaRepository: MapaRepository,
    sessionManager: SessionManager,
    authRepository: AuthRepository,
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val navItems = listOf(
        BottomNavItem.Mapa,
        BottomNavItem.Desafios,
        BottomNavItem.Logros,
        BottomNavItem.Perfil,
        BottomNavItem.Ajustes
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)

    val startDestination = if (isLoggedIn) BottomNavItem.Mapa.route else "login"

    val mostrarBottomBar = currentRoute in navItems.map { it.route }

    val onNodoClick: (NodoCamino) -> Unit = { nodo ->
        when (nodo.tipo) {
            BancoEjercicios.TIPO_BOSS,
            BancoEjercicios.TIPO_BOSS_FINAL -> {
                navController.navigate("boss/${nodo.id}")
            }
            else -> {
                navController.navigate(
                    "ejercicio/${nodo.id}/${Uri.encode(nodo.titulo)}"
                )
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (mostrarBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFF2B1B17),
                    tonalElevation = 0.dp
                ) {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.route

                        val escala by animateFloatAsState(
                            targetValue = if (selected) 1.15f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "iconScale"
                        )

                        val elevacion by animateDpAsState(
                            targetValue = if (selected) 8.dp else 2.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "elevacionNav"
                        )

                        NavigationBarItem(
                            icon = {
                                NavIconoRelieve(
                                    item = item,
                                    selected = selected,
                                    escala = escala,
                                    elevacion = elevacion
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    color = if (selected) Color(0xFFFFD54F)
                                        else Color(0xFFBDBDBD),
                                    fontWeight = if (selected) FontWeight.Bold
                                        else FontWeight.Normal
                                )
                            },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = Color(0xFFFFD54F),
                                unselectedIconColor = Color(0xFFBDBDBD),
                                selectedTextColor = Color(0xFFFFD54F),
                                unselectedTextColor = Color(0xFFBDBDBD)
                            ),
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
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                ) + fadeIn(tween(300))
            },
            exitTransition = { fadeOut(tween(250)) },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                ) + fadeIn(tween(300))
            },
            popExitTransition = { fadeOut(tween(250)) }
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
                    onBackToLogin = { navController.popBackStack() },
                    onEnviar = { correo -> authRepository.enviarCorreoRecuperacion(correo) }
                )
            }

            composable(BottomNavItem.Mapa.route) {
                MapaScreen(
                    viewModel = mapaViewModel,
                    onNodoClick = onNodoClick,
                    onExamenClick = { navController.navigate("examen") }
                )
            }

            composable(BottomNavItem.Desafios.route) {
                val desafiosViewModel: DesafiosViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            DesafiosViewModel(mapaRepository) as T
                    }
                )

                DesafiosScreen(
                    viewModel = desafiosViewModel,
                    onDesafioClick = onNodoClick
                )
            }

            composable("examen") {
                val examenViewModel: ExamenViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ExamenViewModel(mapaRepository) as T
                    }
                )

                ExamenScreen(
                    viewModel = examenViewModel,
                    onExamenComplete = { navController.popBackStack() }
                )
            }

            composable(
                route = "ejercicio/{nodoId}/{titulo}",
                arguments = listOf(
                    navArgument("nodoId") { type = NavType.IntType },
                    navArgument("titulo") { type = NavType.StringType }
                ),
                enterTransition = {
                    slideInVertically(
                        animationSpec = tween(350),
                        initialOffsetY = { it }
                    ) + fadeIn(tween(350))
                },
                popExitTransition = {
                    slideOutVertically(
                        animationSpec = tween(300),
                        targetOffsetY = { it }
                    ) + fadeOut(tween(300))
                }
            ) { backStackEntry ->
                val nodoId = backStackEntry.arguments?.getInt("nodoId") ?: 1
                val titulo = backStackEntry.arguments?.getString("titulo")
                    ?.let { Uri.decode(it) } ?: "Lección"

                val ejercicioViewModel: EjercicioViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            EjercicioViewModel(mapaRepository) as T
                    }
                )

                LaunchedEffect(nodoId) {
                    ejercicioViewModel.cargarEjercicios(nodoId)
                }

                EjercicioScreen(
                    viewModel = ejercicioViewModel,
                    tituloNivel = titulo,
                    onSiguienteEjercicio = {
                        mapaViewModel.finalizarNivelCorrecto(nodoId)
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "boss/{nodoId}",
                arguments = listOf(navArgument("nodoId") { type = NavType.IntType }),
                enterTransition = {
                    slideInVertically(
                        animationSpec = tween(350),
                        initialOffsetY = { it }
                    ) + fadeIn(tween(350))
                },
                popExitTransition = {
                    slideOutVertically(
                        animationSpec = tween(300),
                        targetOffsetY = { it }
                    ) + fadeOut(tween(300))
                }
            ) { backStackEntry ->
                val nodoId = backStackEntry.arguments?.getInt("nodoId") ?: 1

                val bossViewModel: BossViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            BossViewModel(mapaRepository) as T
                    }
                )

                LaunchedEffect(nodoId) {
                    bossViewModel.cargarBoss(nodoId)
                }

                BossScreen(
                    viewModel = bossViewModel,
                    onSalir = { navController.popBackStack() }
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

            composable(BottomNavItem.Ajustes.route) {
                AjustesScreen(
                    sessionManager = sessionManager,
                    authRepository = authRepository,
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChange = onDarkThemeChange,
                    onAccountDeleted = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

/**
 * Chip de ícono con efecto 3D para la navbar: sombra, borde claro arriba
 * y oscuro abajo; el seleccionado se eleva más y toma tono dorado.
 */
@Composable
private fun NavIconoRelieve(
    item: BottomNavItem,
    selected: Boolean,
    escala: Float,
    elevacion: Dp
) {
    val colorContenido = if (selected) Color(0xFFFFD54F) else Color(0xFFBDBDBD)
    val colorFondo = if (selected) Color(0xFF5D4037) else Color(0xFF3E2723)

    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = elevacion,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (selected) 0.55f else 0.20f),
                    Color.Black.copy(alpha = 0.55f)
                )
            )
        ),
        color = colorFondo
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            item.icon?.let {
                Icon(
                    it,
                    contentDescription = item.title,
                    tint = colorContenido,
                    modifier = Modifier.scale(escala)
                )
            } ?: Text(
                text = item.emoji ?: "",
                fontSize = 20.sp,
                modifier = Modifier.scale(escala)
            )
        }
    }
}
