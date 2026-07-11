package com.example.AppRazer.presentation.screens.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val registerSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = "") }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = "") }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = "") }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = "") }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    fun getGoogleSignInIntent() = authRepository.getGoogleSignInIntent()

    fun registerWithEmail() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            val result = authRepository.registerWithEmail(state.email, state.password)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
            } else {
                val message = when {
                    result.exceptionOrNull()?.message?.contains("email") == true -> "El email ya está en uso"
                    result.exceptionOrNull()?.message?.contains("password") == true -> "La contraseña debe tener al menos 6 caracteres"
                    else -> "Error al crear la cuenta"
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            val result = authRepository.loginWithGoogle(idToken)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error con Google: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun setGoogleError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }
}