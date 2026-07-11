package com.example.AppRazer.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.remote.firebase.firestore.CartRepository
import com.example.AppRazer.data.remote.firebase.firestore.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val visible: Boolean = false,
    val selectedMethod: String = "",
    val isProcessing: Boolean = false,
    val paymentDone: Boolean = false,
    val cardNumber: String = "",
    val cardName: String = "",
    val cardExpiry: String = "",
    val cardCvv: String = "",
    val showCardForm: Boolean = false,
    val cvvFocused: Boolean = false,
    val coupon: String = "",
    val couponApplied: Boolean = false
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(100)
            _uiState.update { it.copy(visible = true) }
        }
    }

    fun selectMethod(method: String) {
        _uiState.update { it.copy(selectedMethod = method, showCardForm = method == "card") }
    }

    fun onCardNumberChange(value: String) {
        if (value.length <= 16) _uiState.update { it.copy(cardNumber = value.filter { c -> c.isDigit() }) }
    }

    fun onCardNameChange(value: String) {
        _uiState.update { it.copy(cardName = value) }
    }

    fun onCardExpiryChange(value: String) {
        if (value.length <= 5) {
            val formatted = value.filter { c -> c.isDigit() || c == '/' }
                .let { v -> if (v.length == 2 && !v.contains('/')) "$v/" else v }
            _uiState.update { it.copy(cardExpiry = formatted) }
        }
    }

    fun onCardCvvChange(value: String) {
        if (value.length <= 3) _uiState.update { it.copy(cardCvv = value.filter { c -> c.isDigit() }) }
    }

    fun onCvvFocusChanged(focused: Boolean) {
        _uiState.update { it.copy(cvvFocused = focused) }
    }

    fun onCouponChange(value: String) {
        _uiState.update { it.copy(coupon = value) }
    }

    fun applyCoupon() {
        if (_uiState.value.coupon.isNotEmpty()) {
            _uiState.update { it.copy(couponApplied = true) }
        }
    }

    fun pay(total: Double) {
        val state = _uiState.value
        if (state.isProcessing || state.paymentDone || state.selectedMethod.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            orderRepository.createOrder(
                items = CartState.items.toList(),
                total = total,
                paymentMethod = state.selectedMethod
            )
            cartRepository.clearCart()
            CartState.clearCart()

            delay(2000) // simula proceso de pago
            _uiState.update { it.copy(isProcessing = false, paymentDone = true) }
        }
    }
}