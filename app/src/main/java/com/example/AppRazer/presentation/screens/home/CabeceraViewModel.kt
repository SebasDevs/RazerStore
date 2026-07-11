package com.example.AppRazer.presentation.screens.home

import androidx.lifecycle.ViewModel
import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CabeceraViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val isLoggedIn: Boolean get() = authRepository.isLoggedIn
}