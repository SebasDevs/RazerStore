package com.example.AppRazer.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.local.GuestCartRepository
import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import com.example.AppRazer.data.remote.firebase.firestore.OrderRepository
import com.example.AppRazer.presentation.screens.cart.CartState
import com.example.AppRazer.presentation.screens.wishlist.WishlistState
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: FirebaseUser? = null,
    val orderCount: Int = 0,
    val visible: Boolean = false,
    val loggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    private val guestCartRepository: GuestCartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(user = authRepository.currentUser))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val result = orderRepository.getOrders()
            val count = if (result.isSuccess) result.getOrDefault(emptyList()).size else 0
            _uiState.update { it.copy(orderCount = count, visible = true) }
        }
    }

    fun logout() {
        authRepository.logout()
        CartState.clearCart()
        WishlistState.setAll(emptySet())
        viewModelScope.launch {
            guestCartRepository.clearCart()
        }
        _uiState.update { it.copy(loggedOut = true) }
    }
}