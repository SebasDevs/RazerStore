package com.example.AppRazer.presentation.screens.product

import com.example.AppRazer.data.remote.firebase.firestore.CartRepository
import com.example.AppRazer.presentation.screens.cart.CartState
import com.example.AppRazer.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

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

    private fun createViewModel() = ProductDetailViewModel(cartRepository)

    @Test
    fun `selectColor actualiza el color seleccionado`() {
        val viewModel = createViewModel()

        viewModel.selectColor("Mercury")

        assertEquals("Mercury", viewModel.uiState.value.selectedColor)
    }

    @Test
    fun `addToCart agrega el item al estado global y dispara navigateToCart`() = runTest {
        coEvery { cartRepository.addItem(any()) } returns Result.success(Unit)

        val viewModel = createViewModel()
        viewModel.addToCart("blade14", "Razer Blade 14", 2499.99, "url")
        advanceUntilIdle()

        assertEquals(1, CartState.items.size)
        assertEquals("blade14", CartState.items.first().id)
        assertTrue(viewModel.uiState.value.addedToCart)
        assertTrue(viewModel.uiState.value.navigateToCart)
        assertFalse(viewModel.uiState.value.isAddingToCart)
        coVerify { cartRepository.addItem(any()) }
    }

    @Test
    fun `addToCart no hace nada si ya se agrego antes`() = runTest {
        coEvery { cartRepository.addItem(any()) } returns Result.success(Unit)

        val viewModel = createViewModel()
        viewModel.addToCart("blade14", "Razer Blade 14", 2499.99, "url")
        advanceUntilIdle()

        viewModel.addToCart("blade14", "Razer Blade 14", 2499.99, "url") // segundo intento
        advanceUntilIdle()

        // solo debe haberse guardado una vez en Firestore
        coVerify(exactly = 1) { cartRepository.addItem(any()) }
    }

    @Test
    fun `onNavigatedToCart resetea la bandera de navegacion`() = runTest {
        coEvery { cartRepository.addItem(any()) } returns Result.success(Unit)

        val viewModel = createViewModel()
        viewModel.addToCart("blade14", "Razer Blade 14", 2499.99, "url")
        advanceUntilIdle()

        viewModel.onNavigatedToCart()

        assertFalse(viewModel.uiState.value.navigateToCart)
    }
}