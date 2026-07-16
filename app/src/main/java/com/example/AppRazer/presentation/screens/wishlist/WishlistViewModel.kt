package com.example.AppRazer.presentation.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.local.GuestWishlistRepository
import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import com.example.AppRazer.data.remote.firebase.firestore.WishlistItem
import com.example.AppRazer.data.remote.firebase.firestore.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WishlistUiState(
    val items: List<WishlistItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val guestWishlistRepository: GuestWishlistRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    private val isLoggedIn get() = authRepository.isLoggedIn

    init {
        loadWishlist()
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            val items = if (isLoggedIn) {
                val result = wishlistRepository.getWishlist()
                if (result.isSuccess) result.getOrDefault(emptyList()) else emptyList()
            } else {
                guestWishlistRepository.getWishlist()
            }
            WishlistState.setAll(items.map { it.id }.toSet())
            _uiState.update { it.copy(items = items, isLoading = false) }
        }
    }

    fun toggleFavorite(item: WishlistItem) {
        val alreadyFavorite = WishlistState.isFavorite(item.id)
        WishlistState.toggle(item.id)

        viewModelScope.launch {
            if (alreadyFavorite) {
                if (isLoggedIn) wishlistRepository.removeItem(item.id)
                _uiState.update { state -> state.copy(items = state.items.filterNot { it.id == item.id }) }
            } else {
                if (isLoggedIn) wishlistRepository.addItem(item)
                _uiState.update { state -> state.copy(items = state.items + item) }
            }

            if (!isLoggedIn) {
                guestWishlistRepository.saveWishlist(_uiState.value.items)
            }
        }
    }

    fun removeFromWishlist(itemId: String) {
        WishlistState.toggle(itemId)
        viewModelScope.launch {
            if (isLoggedIn) wishlistRepository.removeItem(itemId)
            _uiState.update { state -> state.copy(items = state.items.filterNot { it.id == itemId }) }
            if (!isLoggedIn) {
                guestWishlistRepository.saveWishlist(_uiState.value.items)
            }
        }
    }
}