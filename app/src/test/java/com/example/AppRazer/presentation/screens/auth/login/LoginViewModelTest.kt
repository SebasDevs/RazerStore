package com.example.AppRazer.presentation.screens.auth.login

import app.cash.turbine.test
import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import com.example.AppRazer.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository: AuthRepository = mockk()

    private fun createViewModel() = LoginViewModel(authRepository)

    @Test
    fun `estado inicial esta vacio y sin errores`() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isLoading)
        assertEquals("", state.errorMessage)
        assertFalse(state.loginSuccess)
    }

    @Test
    fun `onEmailChange actualiza el email y limpia el error`() {
        val viewModel = createViewModel()

        viewModel.onEmailChange("sebastian@razer.com")

        assertEquals("sebastian@razer.com", viewModel.uiState.value.email)
        assertEquals("", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `login exitoso marca loginSuccess como true`() = runTest {
        val fakeUser = mockk<FirebaseUser>()
        coEvery { authRepository.loginWithEmail(any(), any()) } returns Result.success(fakeUser)

        val viewModel = createViewModel()
        viewModel.onEmailChange("sebastian@razer.com")
        viewModel.onPasswordChange("123456")

        viewModel.uiState.test {
            awaitItem() // estado actual antes de llamar al login

            viewModel.loginWithEmail()

            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val result = awaitItem()
            assertFalse(result.isLoading)
            assertTrue(result.loginSuccess)
        }
    }

    @Test
    fun `login fallido con password incorrecta muestra mensaje adecuado`() = runTest {
        coEvery { authRepository.loginWithEmail(any(), any()) } returns
                Result.failure(Exception("The password is invalid"))

        val viewModel = createViewModel()
        viewModel.onEmailChange("sebastian@razer.com")
        viewModel.onPasswordChange("wrong")

        viewModel.uiState.test {
            awaitItem()

            viewModel.loginWithEmail()

            awaitItem() // isLoading = true

            val result = awaitItem()
            assertFalse(result.isLoading)
            assertEquals("Contraseña incorrecta", result.errorMessage)
            assertFalse(result.loginSuccess)
        }
    }

    @Test
    fun `togglePasswordVisibility invierte el estado`() {
        val viewModel = createViewModel()

        assertFalse(viewModel.uiState.value.passwordVisible)
        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.uiState.value.passwordVisible)
    }
}