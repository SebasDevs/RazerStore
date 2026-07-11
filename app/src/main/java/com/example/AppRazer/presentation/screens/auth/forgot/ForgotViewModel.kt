package com.example.AppRazer.presentation.screens.auth.forgot

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

data class ForgotUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val emailSent: Boolean = false
)

@HiltViewModel
class ForgotViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotUiState())
    val uiState: StateFlow<ForgotUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = "") }
    }

    fun sendResetEmail() {
        val currentEmail = _uiState.value.email
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            val result = authRepository.resetPassword(currentEmail)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, emailSent = true) }
            } else {
                val message = when {
                    result.exceptionOrNull()?.message?.contains("user") == true ->
                        "No existe ninguna cuenta con ese email"

                    result.exceptionOrNull()?.message?.contains("email") == true ->
                        "Email no válido"

                    else -> "Error al enviar el email"
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    fun resendEmail() {
        viewModelScope.launch {
            authRepository.resetPassword(_uiState.value.email)
        }
    }
}