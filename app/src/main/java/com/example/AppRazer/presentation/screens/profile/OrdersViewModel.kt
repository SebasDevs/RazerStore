package com.example.AppRazer.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.remote.firebase.firestore.Order
import com.example.AppRazer.data.remote.firebase.firestore.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val result = orderRepository.getOrders()
            _uiState.update {
                it.copy(
                    orders = if (result.isSuccess) result.getOrDefault(emptyList()) else emptyList(),
                    isLoading = false
                )
            }
        }
    }
}