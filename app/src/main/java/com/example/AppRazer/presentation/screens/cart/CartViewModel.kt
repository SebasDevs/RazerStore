package com.example.AppRazer.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.local.GuestCartRepository
import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import com.example.AppRazer.data.remote.firebase.firestore.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val guestCartRepository: GuestCartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val isLoggedIn get() = authRepository.isLoggedIn

    init {
        viewModelScope.launch {
            val items = if (isLoggedIn) {
                cartRepository.getCart().getOrDefault(emptyList())
            } else {
                guestCartRepository.getCart()
            }
            CartState.items.clear()
            CartState.items.addAll(items)
            _isLoading.value = false
        }
    }

    private fun persist() {
        viewModelScope.launch {
            if (isLoggedIn) {
                // ── Firestore ya se actualiza item por item en cada acción ──
            } else {
                guestCartRepository.saveCart(CartState.items.toList())
            }
        }
    }

    fun clearCart() {
        CartState.clearCart()
        viewModelScope.launch {
            if (isLoggedIn) cartRepository.clearCart() else guestCartRepository.clearCart()
        }
    }

    fun increaseQuantity(item: CartItem) {
        CartState.increaseQuantity(item)
        viewModelScope.launch {
            if (isLoggedIn) {
                cartRepository.updateQuantity(item.id, item.quantity + 1)
            } else {
                guestCartRepository.saveCart(CartState.items.toList())
            }
        }
    }

    fun decreaseQuantity(item: CartItem) {
        CartState.decreaseQuantity(item)
        viewModelScope.launch {
            if (isLoggedIn) {
                if (item.quantity > 1) cartRepository.updateQuantity(item.id, item.quantity - 1)
                else cartRepository.removeItem(item.id)
            } else {
                guestCartRepository.saveCart(CartState.items.toList())
            }
        }
    }

    fun removeItem(item: CartItem) {
        CartState.removeItem(item)
        viewModelScope.launch {
            if (isLoggedIn) cartRepository.removeItem(item.id)
            else guestCartRepository.saveCart(CartState.items.toList())
        }
    }
}