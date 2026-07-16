package com.example.AppRazer.presentation.screens.wishlist

import androidx.compose.runtime.mutableStateOf

object WishlistState {
    val favoriteIds = mutableStateOf<Set<String>>(emptySet())

    fun isFavorite(productId: String): Boolean = favoriteIds.value.contains(productId)

    fun toggle(productId: String) {
        favoriteIds.value = if (favoriteIds.value.contains(productId)) {
            favoriteIds.value - productId
        } else {
            favoriteIds.value + productId
        }
    }

    fun setAll(ids: Set<String>) {
        favoriteIds.value = ids
    }
}