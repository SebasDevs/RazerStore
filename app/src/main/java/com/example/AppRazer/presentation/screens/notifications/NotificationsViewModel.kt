package com.example.AppRazer.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppRazer.data.local.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: AppSettingsRepository
) : ViewModel() {

    val orderUpdates = repository.orderUpdatesFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, true
    )
    val promotions = repository.promotionsFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, true
    )
    val productNews = repository.productNewsFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, false
    )

    fun setOrderUpdates(enabled: Boolean) {
        viewModelScope.launch { repository.setOrderUpdates(enabled) }
    }

    fun setPromotions(enabled: Boolean) {
        viewModelScope.launch { repository.setPromotions(enabled) }
    }

    fun setProductNews(enabled: Boolean) {
        viewModelScope.launch { repository.setProductNews(enabled) }
    }
}