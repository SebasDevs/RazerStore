package com.example.AppRazer.presentation.screens.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.AppRazer.data.model.ProductRepository
import com.example.AppRazer.presentation.screens.home.ProductInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class SortOption(val label: String) {
    NONE("Relevancia"),
    PRICE_ASC("Precio: menor a mayor"),
    PRICE_DESC("Precio: mayor a menor"),
    ALPHABETICAL("Nombre A-Z")
}

data class CategoryProductsUiState(
    val category: String = "",
    val allProducts: List<ProductInfo> = emptyList(),
    val searchQuery: String = "",
    val minPrice: Float = 0f,
    val maxPrice: Float = 10000f,
    val sortOption: SortOption = SortOption.NONE
) {
    val filteredProducts: List<ProductInfo>
        get() {
            var result = allProducts.filter {
                it.priceValue >= minPrice && it.priceValue <= maxPrice
            }
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase().trim()
                result = result.filter { it.title.lowercase().contains(q) }
            }
            result = when (sortOption) {
                SortOption.PRICE_ASC -> result.sortedBy { it.priceValue }
                SortOption.PRICE_DESC -> result.sortedByDescending { it.priceValue }
                SortOption.ALPHABETICAL -> result.sortedBy { it.title }
                SortOption.NONE -> result
            }
            return result
        }
}

@HiltViewModel
class CategoryProductsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val category: String = savedStateHandle.get<String>("category") ?: ""

    private val _uiState = MutableStateFlow(
        CategoryProductsUiState(
            category = category,
            allProducts = ProductRepository.getByCategory(category)
        )
    )
    val uiState: StateFlow<CategoryProductsUiState> = _uiState.asStateFlow()

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onPriceRangeChange(min: Float, max: Float) {
        _uiState.update { it.copy(minPrice = min, maxPrice = max) }
    }

    fun onSortChange(sort: SortOption) {
        _uiState.update { it.copy(sortOption = sort) }
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                minPrice = 0f,
                maxPrice = 10000f,
                sortOption = SortOption.NONE
            )
        }
    }
}