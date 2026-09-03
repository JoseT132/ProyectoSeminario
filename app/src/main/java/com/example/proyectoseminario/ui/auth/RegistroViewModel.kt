package com.example.proyectoseminario.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoseminario.data.local.PerfilUsuario
import com.example.proyectoseminario.data.preferences.SessionManager
import com.example.proyectoseminario.repository.AuthRepository
import com.example.proyectoseminario.utils.SecurityUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nombre)
    }

    fun onCorreoChange(correo: String) {
        _uiState.value = _uiState.value.copy(correo = correo)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    fun onFechaNacimientoChange(fechaNacimiento: String) {
        _uiState.value = _uiState.value.copy(fechaNacimiento = fechaNacimiento)
    }

    fun onNivelEscolarChange(nivelEscolar: String) {
        _uiState.value = _uiState.value.copy(nivelEscolar = nivelEscolar)
    }

    fun registrar(onSuccess: () -> Unit) {
        val state = _uiState.value
        val nombre = state.nombre.trim()
        val correo = state.correo.trim()

        _uiState.value = state.copy(error = null)

        when {
            nombre.isBlank() -> {
                _uiState.value = state.copy(error = "El nombre es obligatorio")
                return
            }
            correo.isBlank() -> {
                _uiState.value = state.copy(error = "El correo es obligatorio")
                return
            }
            !SecurityUtils.isValidEmail(correo) -> {
                _uiState.value = state.copy(error = "El correo no tiene un formato válido")
                return
            }
            state.fechaNacimiento.isBlank() -> {
                _uiState.value = state.copy(error = "La fecha de nacimiento es obligatoria")
                return
            }
            state.password.length < 6 -> {
                _uiState.value = state.copy(error = "La contraseña debe tener al menos 6 caracteres")
                return
            }
            state.password != state.confirmPassword -> {
                _uiState.value = state.copy(error = "Las contraseñas no coinciden")
                return
            }
        }

        _uiState.value = state.copy(isLoading = true)

        viewModelScope.launch {
            val result = authRepository.registrarUsuario(
                nombre = nombre,
                correo = correo,
                password = state.password,
                fechaNacimiento = state.fechaNacimiento,
                nivelEscolar = state.nivelEscolar
            )

            _uiState.value = _uiState.value.copy(isLoading = false)

            result.fold(
                onSuccess = { perfil ->
                    sessionManager.saveSession(
                        userId = perfil.id,
                        email = perfil.correo,
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

    data class RegistroUiState(
        val nombre: String = "",
        val correo: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val fechaNacimiento: String = "",
        val nivelEscolar: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
