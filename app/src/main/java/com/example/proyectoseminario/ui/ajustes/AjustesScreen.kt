package com.example.proyectoseminario.ui.ajustes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectoseminario.data.preferences.SessionManager
import com.example.proyectoseminario.repository.AuthRepository
import com.example.proyectoseminario.ui.components.BotonRelieve
import com.example.proyectoseminario.utils.GoogleSignInHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    sessionManager: SessionManager,
    authRepository: AuthRepository,
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onAccountDeleted: () -> Unit
) {
    val userId by sessionManager.currentUserId.collectAsState(initial = 0)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPoliticasDialog by remember { mutableStateOf(false) }
    var showSeguridadDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Modo oscuro")
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onDarkThemeChange
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Información",
                        style = MaterialTheme.typography.titleMedium
                    )
                    BotonRelieve(
                        texto = "Políticas de Privacidad",
                        emoji = "📜",
                        onClick = { showPoliticasDialog = true }
                    )
                    BotonRelieve(
                        texto = "Seguridad de Datos",
                        emoji = "🛡",
                        onClick = { showSeguridadDialog = true }
                    )
                }
            }

            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar cuenta")
            }
        }

        if (showPoliticasDialog) {
            AlertDialog(
                onDismissRequest = { showPoliticasDialog = false },
                title = { Text("Políticas de Privacidad") },
                text = {
                    Text(
                        "• Guardamos tu nombre, correo, fecha de nacimiento, nivel escolar " +
                            "y tu progreso en las lecciones.\n\n" +
                            "• Tus datos se almacenan localmente en tu dispositivo " +
                            "(base de datos Room/SQLite).\n\n" +
                            "• Si inicias sesión con Google, Firebase Authentication " +
                            "gestiona tu identidad de forma segura.\n\n" +
                            "• No compartimos ni vendemos tu información a terceros.\n\n" +
                            "• Al eliminar tu cuenta desde Ajustes se borra tu perfil " +
                            "y tu progreso local de forma permanente."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showPoliticasDialog = false }) {
                        Text("Entendido")
                    }
                }
            )
        }

        if (showSeguridadDialog) {
            AlertDialog(
                onDismissRequest = { showSeguridadDialog = false },
                title = { Text("Seguridad de Datos") },
                text = {
                    Text(
                        "• Tu contraseña nunca se guarda en texto plano: se protege " +
                            "con hash BCrypt antes de almacenarse.\n\n" +
                            "• La sesión se mantiene localmente mediante DataStore " +
                            "en el almacenamiento privado de la app.\n\n" +
                            "• Con Google Sign-In, la autenticación la realiza Google " +
                            "directamente; la app nunca ve tu contraseña de Google.\n\n" +
                            "• Te recomendamos usar contraseñas de al menos 8 " +
                            "caracteres con letras y números.\n\n" +
                            "• Puedes eliminar todos tus datos en cualquier momento " +
                            "con la opción \"Eliminar cuenta\"."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showSeguridadDialog = false }) {
                        Text("Entendido")
                    }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar cuenta") },
                text = { Text("¿Estás seguro? Se borrarán tus datos y no se podrán recuperar.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                if (userId > 0) authRepository.eliminarCuenta(userId)
                                GoogleSignInHelper.cerrarSesion(context)
                                sessionManager.clearSession()
                                showDeleteDialog = false
                                onAccountDeleted()
                            }
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
