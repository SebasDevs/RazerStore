package com.example.AppRazer.presentation.screens.auth.register

import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import com.example.AppRazer.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
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
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository: AuthRepository = mockk()

    private fun createViewModel() = RegisterViewModel(authRepository)

    @Test
    fun `estado inicial esta vacio`() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertEquals("", state.username)
        assertEquals("", state.email)
        assertFalse(state.registerSuccess)
    }

    @Test
    fun `toggles de visibilidad de contrasena funcionan por separado`() {
        val viewModel = createViewModel()

        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.uiState.value.passwordVisible)
        assertFalse(viewModel.uiState.value.confirmPasswordVisible)

        viewModel.toggleConfirmPasswordVisibility()
        assertTrue(viewModel.uiState.value.confirmPasswordVisible)
    }

    @Test
    fun `registro exitoso marca registerSuccess`() = runTest {
        val fakeUser = mockk<FirebaseUser>()
        coEvery { authRepository.registerWithEmail(any(), any()) } returns Result.success(fakeUser)

        val viewModel = createViewModel()
        viewModel.onEmailChange("sebastian@razer.com")
        viewModel.onPasswordChange("123456")

        viewModel.registerWithEmail()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.registerSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `registro fallido por email en uso muestra el mensaje correcto`() = runTest {
        coEvery { authRepository.registerWithEmail(any(), any()) } returns
                Result.failure(Exception("The email address is already in use"))

        val viewModel = createViewModel()
        viewModel.onEmailChange("sebastian@razer.com")
        viewModel.onPasswordChange("123456")

        viewModel.registerWithEmail()
        advanceUntilIdle()

        assertEquals("El email ya está en uso", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.registerSuccess)
    }

    @Test
    fun `login con Google exitoso marca registerSuccess`() = runTest {
        val fakeUser = mockk<FirebaseUser>()
        coEvery { authRepository.loginWithGoogle(any()) } returns Result.success(fakeUser)

        val viewModel = createViewModel()
        viewModel.loginWithGoogle("fake-token")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.registerSuccess)
    }
}