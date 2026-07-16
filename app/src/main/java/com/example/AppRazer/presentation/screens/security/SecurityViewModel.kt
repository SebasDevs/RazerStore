package com.example.AppRazer.presentation.screens.security

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

data class SecurityUiState(
    val isEmailUser: Boolean = false,
    val isEmailVerified: Boolean = false,
    val userEmail: String = "",

    // ── Cambiar contraseña ──
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = "",

    // ── Verificación de email ──
    val isSendingVerification: Boolean = false,
    val verificationSent: Boolean = false,

    // ── Eliminar cuenta ──
    val showDeleteDialog: Boolean = false,
    val deletePassword: String = "",
    val isDeletingAccount: Boolean = false,
    val deleteError: String = "",
    val accountDeleted: Boolean = false
)

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SecurityUiState(
            isEmailUser = authRepository.isEmailPasswordUser,
            isEmailVerified = authRepository.isEmailVerified,
            userEmail = authRepository.currentUser?.email ?: ""
        )
    )
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    // ── Cambiar contraseña ──────────────────────────────────────
    fun onCurrentPasswordChange(value: String) =
        _uiState.update { it.copy(currentPassword = value, errorMessage = "", successMessage = "") }

    fun onNewPasswordChange(value: String) =
        _uiState.update { it.copy(newPassword = value, errorMessage = "", successMessage = "") }

    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value, errorMessage = "", successMessage = "") }

    fun changePassword() {
        val state = _uiState.value

        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "La nueva contraseña debe tener al menos 6 caracteres") }
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "", successMessage = "") }
            val result = authRepository.changePassword(state.currentPassword, state.newPassword)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Contraseña actualizada correctamente",
                        currentPassword = "", newPassword = "", confirmPassword = ""
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Contraseña actual incorrecta"
                    )
                }
            }
        }
    }

    // ── Verificación de email ────────────────────────────────────
    fun sendVerificationEmail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingVerification = true) }
            authRepository.sendEmailVerification()
            _uiState.update { it.copy(isSendingVerification = false, verificationSent = true) }
        }
    }

    fun refreshVerificationStatus() {
        viewModelScope.launch {
            authRepository.reloadUser()
            _uiState.update { it.copy(isEmailVerified = authRepository.isEmailVerified) }
        }
    }

    // ── Eliminar cuenta ───────────────────────────────────────────
    fun openDeleteDialog() = _uiState.update { it.copy(showDeleteDialog = true, deleteError = "") }
    fun closeDeleteDialog() =
        _uiState.update { it.copy(showDeleteDialog = false, deletePassword = "") }

    fun onDeletePasswordChange(value: String) =
        _uiState.update { it.copy(deletePassword = value, deleteError = "") }

    fun confirmDeleteAccount() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, deleteError = "") }
            val result = authRepository.deleteAccount(
                if (state.isEmailUser) state.deletePassword else null
            )
            if (result.isSuccess) {
                _uiState.update { it.copy(isDeletingAccount = false, accountDeleted = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isDeletingAccount = false,
                        deleteError = "Contraseña incorrecta o error al eliminar la cuenta"
                    )
                }
            }
        }
    }
}