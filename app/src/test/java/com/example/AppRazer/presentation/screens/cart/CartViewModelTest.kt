package com.example.AppRazer.presentation.screens.cart

import com.example.AppRazer.data.remote.firebase.firestore.CartRepository
import com.example.AppRazer.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val cartRepository: CartRepository = mockk()

    @Before
    fun setup() {
        CartState.items.clear()
    }

    @After
    fun tearDown() {
        CartState.items.clear()
    }

    @Test
    fun `al iniciar carga el carrito desde Firestore y deja de estar loading`() = runTest {
        val item = CartItem(id = "1", name = "Razer Mouse", price = 100.0, imageUrl = "url")
        coEvery { cartRepository.getCart() } returns Result.success(listOf(item))

        val viewModel = CartViewModel(cartRepository)
        advanceUntilIdle()

        assertEquals(1, CartState.items.size)
        assertEquals("Razer Mouse", CartState.items.first().name)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `increaseQuantity aumenta la cantidad y actualiza Firestore`() = runTest {
        val item =
            CartItem(id = "1", name = "Razer Mouse", price = 100.0, imageUrl = "url", quantity = 1)
        coEvery { cartRepository.getCart() } returns Result.success(listOf(item))
        coEvery { cartRepository.updateQuantity(any(), any()) } returns Result.success(Unit)

        val viewModel = CartViewModel(cartRepository)
        advanceUntilIdle()

        val loadedItem = CartState.items.first()
        viewModel.increaseQuantity(loadedItem)
        advanceUntilIdle()

        assertEquals(2, CartState.items.first().quantity)
        coVerify { cartRepository.updateQuantity("1", 2) }
    }

    @Test
    fun `decreaseQuantity con cantidad 1 elimina el item y lo borra de Firestore`() = runTest {
        val item =
            CartItem(id = "1", name = "Razer Mouse", price = 100.0, imageUrl = "url", quantity = 1)
        coEvery { cartRepository.getCart() } returns Result.success(listOf(item))
        coEvery { cartRepository.removeItem(any()) } returns Result.success(Unit)

        val viewModel = CartViewModel(cartRepository)
        advanceUntilIdle()

        val loadedItem = CartState.items.first()
        viewModel.decreaseQuantity(loadedItem)
        advanceUntilIdle()

        assertTrue(CartState.items.isEmpty())
        coVerify { cartRepository.removeItem("1") }
    }

    @Test
    fun `clearCart vacia el estado local y Firestore`() = runTest {
        val item = CartItem(id = "1", name = "Razer Mouse", price = 100.0, imageUrl = "url")
        coEvery { cartRepository.getCart() } returns Result.success(listOf(item))
        coEvery { cartRepository.clearCart() } returns Result.success(Unit)

        val viewModel = CartViewModel(cartRepository)
        advanceUntilIdle()

        viewModel.clearCart()
        advanceUntilIdle()

        assertTrue(CartState.items.isEmpty())
        coVerify { cartRepository.clearCart() }
    }
}