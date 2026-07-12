package com.example.AppRazer.presentation.screens.profile

import com.example.AppRazer.data.remote.firebase.firestore.Order
import com.example.AppRazer.data.remote.firebase.firestore.OrderRepository
import com.example.AppRazer.util.MainDispatcherRule
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
class OrdersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val orderRepository: OrderRepository = mockk()

    @Test
    fun `carga los pedidos correctamente y deja de estar loading`() = runTest {
        val fakeOrders = listOf(mockk<Order>(), mockk<Order>(), mockk<Order>())
        coEvery { orderRepository.getOrders() } returns Result.success(fakeOrders)

        val viewModel = OrdersViewModel(orderRepository)
        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.orders.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `si falla la carga, la lista queda vacia sin crashear`() = runTest {
        coEvery { orderRepository.getOrders() } returns Result.failure(Exception("network error"))

        val viewModel = OrdersViewModel(orderRepository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.orders.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
    }
}