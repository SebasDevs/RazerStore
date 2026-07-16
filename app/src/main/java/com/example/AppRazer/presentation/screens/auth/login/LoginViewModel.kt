package com.example.AppRazer.presentation.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.local.GuestCartRepository
import com.example.AppRazer.data.local.GuestWishlistRepository
import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import com.example.AppRazer.data.remote.firebase.firestore.CartRepository
import com.example.AppRazer.data.remote.firebase.firestore.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Estado de la pantalla, sellado y explícito ──────────────────
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val loginSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository,
    private val guestWishlistRepository: GuestWishlistRepository,
    private val guestCartRepository: GuestCartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = "") }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = "") }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun getGoogleSignInIntent() = authRepository.getGoogleSignInIntent()

    fun loginWithEmail() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            val result = authRepository.loginWithEmail(state.email, state.password)
            if (result.isSuccess) {
                mergeGuestCart()
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
            } else {
                val message = when {
                    result.exceptionOrNull()?.message?.contains("password") == true -> "Contraseña incorrecta"
                    result.exceptionOrNull()?.message?.contains("user") == true -> "Usuario no encontrado"
                    else -> "Error al iniciar sesión"
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
                mergeGuestCart()
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
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

    private suspend fun mergeGuestCart() {
        val guestItems = guestCartRepository.getCart()
        if (guestItems.isNotEmpty()) {
            cartRepository.mergeGuestCart(guestItems)
            guestCartRepository.clearCart()
        }
        val guestFavorites = guestWishlistRepository.getWishlist()
        if (guestFavorites.isNotEmpty()) {
            wishlistRepository.mergeGuestWishlist(guestFavorites)
            guestWishlistRepository.clearWishlist()
        }
    }

    fun setGoogleError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }
}