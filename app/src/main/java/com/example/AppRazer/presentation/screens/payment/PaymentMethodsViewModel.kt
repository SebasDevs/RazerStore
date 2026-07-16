package com.example.AppRazer.presentation.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.remote.firebase.firestore.PaymentMethod
import com.example.AppRazer.data.remote.firebase.firestore.PaymentMethodRepository
import com.example.AppRazer.domain.model.detectCardBrand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentMethodsUiState(
    val methods: List<PaymentMethod> = emptyList(),
    val isLoading: Boolean = true,
    val showForm: Boolean = false,
    // ── Campos temporales del formulario (nunca se guardan completos) ──
    val formAlias: String = "",
    val formCardNumber: String = "",
    val formCardName: String = "",
    val formExpiry: String = "",
    val formCvv: String = "",
    val formCvvFocused: Boolean = false,
    val formIsDefault: Boolean = false
)

@HiltViewModel
class PaymentMethodsViewModel @Inject constructor(
    private val repository: PaymentMethodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentMethodsUiState())
    val uiState: StateFlow<PaymentMethodsUiState> = _uiState.asStateFlow()

    init {
        loadMethods()
    }

    private fun loadMethods() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getPaymentMethods()
            _uiState.update {
                it.copy(
                    methods = if (result.isSuccess) result.getOrDefault(emptyList()) else emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun openAddForm() {
        _uiState.update {
            it.copy(
                showForm = true, formAlias = "", formCardNumber = "", formCardName = "",
                formExpiry = "", formCvv = "", formIsDefault = false
            )
        }
    }

    fun closeForm() {
        _uiState.update { it.copy(showForm = false) }
    }

    fun onAliasChange(value: String) = _uiState.update { it.copy(formAlias = value) }
    fun onCardNumberChange(value: String) {
        if (value.length <= 16) _uiState.update { it.copy(formCardNumber = value.filter { c -> c.isDigit() }) }
    }

    fun onCardNameChange(value: String) = _uiState.update { it.copy(formCardName = value) }
    fun onExpiryChange(value: String) {
        if (value.length <= 5) {
            val formatted = value.filter { c -> c.isDigit() || c == '/' }
                .let { v -> if (v.length == 2 && !v.contains('/')) "$v/" else v }
            _uiState.update { it.copy(formExpiry = formatted) }
        }
    }

    fun onCvvChange(value: String) {
        if (value.length <= 3) _uiState.update { it.copy(formCvv = value.filter { c -> c.isDigit() }) }
    }

    fun onCvvFocusChanged(focused: Boolean) = _uiState.update { it.copy(formCvvFocused = focused) }
    fun onDefaultChange(value: Boolean) = _uiState.update { it.copy(formIsDefault = value) }

    fun saveMethod() {
        val state = _uiState.value
        val brand = detectCardBrand(state.formCardNumber)
        val last4 = state.formCardNumber.takeLast(4)

        viewModelScope.launch {
            repository.savePaymentMethod(
                PaymentMethod(
                    alias = state.formAlias.ifBlank { brand.label },
                    last4 = last4,
                    brand = brand.label,
                    expiry = state.formExpiry,
                    isDefault = state.formIsDefault
                )
            )
            closeForm()
            loadMethods()
        }
    }

    fun deleteMethod(id: String) {
        viewModelScope.launch {
            repository.deletePaymentMethod(id)
            loadMethods()
        }
    }
}