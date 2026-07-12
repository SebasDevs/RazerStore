package com.example.AppRazer.presentation.screens.auth.forgot

import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import com.example.AppRazer.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForgotViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository: AuthRepository = mockk()

    private fun createViewModel() = ForgotViewModel(authRepository)

    @Test
    fun `sendResetEmail exitoso marca emailSent`() = runTest {
        coEvery { authRepository.resetPassword(any()) } returns Result.success(Unit)

        val viewModel = createViewModel()
        viewModel.onEmailChange("sebastian@razer.com")
        viewModel.sendResetEmail()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.emailSent)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `sendResetEmail con usuario inexistente muestra el mensaje correcto`() = runTest {
        coEvery { authRepository.resetPassword(any()) } returns
                Result.failure(Exception("There is no user record"))

        val viewModel = createViewModel()
        viewModel.onEmailChange("noexiste@razer.com")
        viewModel.sendResetEmail()
        advanceUntilIdle()

        assertEquals("No existe ninguna cuenta con ese email", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.emailSent)
    }

    @Test
    fun `resendEmail vuelve a llamar al repositorio`() = runTest {
        coEvery { authRepository.resetPassword(any()) } returns Result.success(Unit)

        val viewModel = createViewModel()
        viewModel.onEmailChange("sebastian@razer.com")
        viewModel.resendEmail()
        advanceUntilIdle()

        coVerify { authRepository.resetPassword("sebastian@razer.com") }
    }
}