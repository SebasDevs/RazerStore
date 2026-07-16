package com.example.AppRazer.presentation.screens.addresses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.remote.firebase.firestore.Address
import com.example.AppRazer.data.remote.firebase.firestore.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddressesUiState(
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = true,
    val showForm: Boolean = false,
    val editingAddress: Address? = null
)

@HiltViewModel
class AddressesViewModel @Inject constructor(
    private val repository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressesUiState())
    val uiState: StateFlow<AddressesUiState> = _uiState.asStateFlow()

    init {
        loadAddresses()
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getAddresses()
            _uiState.update {
                it.copy(
                    addresses = if (result.isSuccess) result.getOrDefault(emptyList()) else emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun openAddForm() {
        _uiState.update { it.copy(showForm = true, editingAddress = null) }
    }

    fun openEditForm(address: Address) {
        _uiState.update { it.copy(showForm = true, editingAddress = address) }
    }

    fun closeForm() {
        _uiState.update { it.copy(showForm = false, editingAddress = null) }
    }

    fun saveAddress(address: Address) {
        viewModelScope.launch {
            repository.saveAddress(address)
            closeForm()
            loadAddresses()
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            repository.deleteAddress(addressId)
            loadAddresses()
        }
    }
}