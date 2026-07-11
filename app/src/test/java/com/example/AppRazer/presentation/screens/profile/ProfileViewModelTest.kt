package com.example.AppRazer.presentation.screens.profile

import com.example.AppRazer.data.remote.firebase.auth.AuthRepository
import com.example.AppRazer.data.remote.firebase.firestore.Order
import com.example.AppRazer.data.remote.firebase.firestore.OrderRepository
import com.example.AppRazer.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val orderRepository: OrderRepository = mockk()

    @Test
    fun `al iniciar carga el usuario actual y el numero de pedidos`() = runTest {
        val fakeUser = mockk<FirebaseUser>()
        every { authRepository.currentUser } returns fakeUser
        coEvery { orderRepository.getOrders() } returns Result.success(
            listOf(mockk<Order>(), mockk<Order>())
        )

        val viewModel = ProfileViewModel(authRepository, orderRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeUser, state.user)
        assertEquals(2, state.orderCount)
        assertTrue(state.visible)
    }

    @Test
    fun `si falla la carga de pedidos, orderCount queda en 0`() = runTest {
        every { authRepository.currentUser } returns null
        coEvery { orderRepository.getOrders() } returns Result.failure(Exception("network error"))

        val viewModel = ProfileViewModel(authRepository, orderRepository)
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.orderCount)
        assertTrue(viewModel.uiState.value.visible)
    }

    @Test
    fun `logout cierra sesion y marca loggedOut`() = runTest {
        every { authRepository.currentUser } returns null
        coEvery { orderRepository.getOrders() } returns Result.success(emptyList())

        val viewModel = ProfileViewModel(authRepository, orderRepository)
        advanceUntilIdle()

        viewModel.logout()

        verify { authRepository.logout() }
        assertTrue(viewModel.uiState.value.loggedOut)
    }
}