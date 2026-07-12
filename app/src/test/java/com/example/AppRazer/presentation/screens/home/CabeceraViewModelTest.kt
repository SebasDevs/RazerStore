package com.example.AppRazer.presentation.screens.home

import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CabeceraViewModelTest {

    private val authRepository: AuthRepository = mockk()

    @Test
    fun `isLoggedIn refleja el estado real del AuthRepository`() {
        every { authRepository.isLoggedIn } returns true
        val viewModel = CabeceraViewModel(authRepository)

        assertTrue(viewModel.isLoggedIn)
    }

    @Test
    fun `isLoggedIn es false cuando no hay sesion`() {
        every { authRepository.isLoggedIn } returns false
        val viewModel = CabeceraViewModel(authRepository)

        assertFalse(viewModel.isLoggedIn)
    }
}