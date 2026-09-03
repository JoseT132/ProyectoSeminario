package com.example.proyectoseminario.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.data.local.PerfilUsuario
import com.example.proyectoseminario.data.preferences.SessionManager
import com.example.proyectoseminario.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(correo: String) {
        _uiState.value = _uiState.value.copy(correo = correo)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun iniciarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = authRepository.iniciarSesion(
                _uiState.value.correo.trim(),
                _uiState.value.password
            )

            _uiState.value = _uiState.value.copy(isLoading = false)

            result.fold(
                onSuccess = { perfil ->
                    sessionManager.saveSession(
                        userId = perfil.id,
                        email = perfil.correo ?: "",
                        name = perfil.nombre
                    )
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            )
        }
    }

    data class LoginUiState(
        val correo: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
