package com.example.AppRazer.presentation.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.remote.firebase.firestore.CartRepository
import com.example.AppRazer.presentation.screens.cart.CartItem
import com.example.AppRazer.presentation.screens.cart.CartState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val selectedColor: String = "Negro",
    val isAddingToCart: Boolean = false,
    val addedToCart: Boolean = false,
    val navigateToCart: Boolean = false   // 👈 nuevo: evento de navegación
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun selectColor(color: String) {
        _uiState.update { it.copy(selectedColor = color) }
    }

    fun addToCart(productId: String, title: String, priceValue: Double, imageUrl: String) {
        val state = _uiState.value
        if (state.isAddingToCart || state.addedToCart) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingToCart = true) }
            val item = CartItem(
                id = productId,
                name = title,
                price = priceValue,
                imageUrl = imageUrl
            )
            CartState.addItem(item)
            cartRepository.addItem(item) // guarda en Firestore

            
            _uiState.update {
                it.copy(isAddingToCart = false, addedToCart = true, navigateToCart = true)
            }
        }
    }

    fun onNavigatedToCart() {
        _uiState.update { it.copy(navigateToCart = false) }
    }
}