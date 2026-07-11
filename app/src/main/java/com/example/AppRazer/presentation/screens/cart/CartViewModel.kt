package com.example.AppRazer.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.remote.firebase.firestore.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // ── Cargar carrito desde Firestore al abrir ────────────────
        viewModelScope.launch {
            val result = cartRepository.getCart()
            if (result.isSuccess) {
                CartState.items.clear()
                CartState.items.addAll(result.getOrDefault(emptyList()))
            }
            _isLoading.value = false
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            CartState.clearCart()
            cartRepository.clearCart()
        }
    }

    fun increaseQuantity(item: CartItem) {
        CartState.increaseQuantity(item)
        viewModelScope.launch { cartRepository.updateQuantity(item.id, item.quantity + 1) }
    }

    fun decreaseQuantity(item: CartItem) {
        CartState.decreaseQuantity(item)
        viewModelScope.launch {
            if (item.quantity > 1) cartRepository.updateQuantity(item.id, item.quantity - 1)
            else cartRepository.removeItem(item.id)
        }
    }

    fun removeItem(item: CartItem) {
        CartState.removeItem(item)
        viewModelScope.launch { cartRepository.removeItem(item.id) }
    }
}