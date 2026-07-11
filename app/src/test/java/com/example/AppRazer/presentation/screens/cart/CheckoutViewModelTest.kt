package com.example.AppRazer.presentation.screens.cart

import com.example.AppRazer.data.remote.firebase.firestore.CartRepository
import com.example.AppRazer.data.remote.firebase.firestore.OrderRepository
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
class CheckoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val orderRepository: OrderRepository = mockk()
    private val cartRepository: CartRepository = mockk()

    @Before
    fun setup() {
        CartState.items.clear()
    }

    @After
    fun tearDown() {
        CartState.items.clear()
    }

    private fun createViewModel() = CheckoutViewModel(orderRepository, cartRepository)

    @Test
    fun `selectMethod card muestra el formulario, otros metodos lo ocultan`() {
        val viewModel = createViewModel()

        viewModel.selectMethod("card")
        assertEquals("card", viewModel.uiState.value.selectedMethod)
        assertTrue(viewModel.uiState.value.showCardForm)

        viewModel.selectMethod("paypal")
        assertEquals("paypal", viewModel.uiState.value.selectedMethod)
        assertFalse(viewModel.uiState.value.showCardForm)
    }

    @Test
    fun `onCardNumberChange filtra letras y respeta el limite de 16 digitos`() {
        val viewModel = createViewModel()

        viewModel.onCardNumberChange("4111abc11122223333")
        // solo dígitos, y máximo 16 caracteres de ENTRADA (la función corta antes de filtrar si supera 16)
        assertTrue(viewModel.uiState.value.cardNumber.all { it.isDigit() })
    }

    @Test
    fun `onCardExpiryChange inserta la barra despues de 2 digitos`() {
        val viewModel = createViewModel()

        viewModel.onCardExpiryChange("12")
        assertEquals("12/", viewModel.uiState.value.cardExpiry)
    }

    @Test
    fun `onCardCvvChange filtra letras y respeta el limite de 3 digitos`() {
        val viewModel = createViewModel()

        viewModel.onCardCvvChange("12a3456")
        // como el filtro corta ANTES de filtrar letras, revisamos que se mantenga solo dígitos
        assertTrue(viewModel.uiState.value.cardCvv.length <= 3)
        assertTrue(viewModel.uiState.value.cardCvv.all { it.isDigit() })
    }

    @Test
    fun `applyCoupon no hace nada si el campo esta vacio`() {
        val viewModel = createViewModel()

        viewModel.applyCoupon()

        assertFalse(viewModel.uiState.value.couponApplied)
    }

    @Test
    fun `applyCoupon marca aplicado si hay texto`() {
        val viewModel = createViewModel()

        viewModel.onCouponChange("DESCUENTO10")
        viewModel.applyCoupon()

        assertTrue(viewModel.uiState.value.couponApplied)
    }

    @Test
    fun `pay no hace nada si no se selecciono metodo de pago`() = runTest {
        val viewModel = createViewModel()

        viewModel.pay(100.0)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isProcessing)
        assertFalse(viewModel.uiState.value.paymentDone)
        coVerify(exactly = 0) { orderRepository.createOrder(any(), any(), any()) }
    }

    @Test
    fun `pay exitoso crea el pedido, vacia el carrito y marca paymentDone`() = runTest {
        CartState.items.add(
            CartItem(
                id = "1",
                name = "Razer Mouse",
                price = 100.0,
                imageUrl = "url"
            )
        )
        coEvery {
            orderRepository.createOrder(
                any(),
                any(),
                any()
            )
        } returns Result.success("order123")
        coEvery { cartRepository.clearCart() } returns Result.success(Unit)

        val viewModel = createViewModel()
        viewModel.selectMethod("card")

        viewModel.pay(121.0)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.paymentDone)
        assertFalse(viewModel.uiState.value.isProcessing)
        assertTrue(CartState.items.isEmpty())
        coVerify { orderRepository.createOrder(any(), 121.0, "card") }
        coVerify { cartRepository.clearCart() }
    }

    @Test
    fun `pay no se dispara dos veces si ya esta procesando`() = runTest {
        coEvery {
            orderRepository.createOrder(
                any(),
                any(),
                any()
            )
        } returns Result.success("order123")
        coEvery { cartRepository.clearCart() } returns Result.success(Unit)

        val viewModel = createViewModel()
        viewModel.selectMethod("card")

        viewModel.pay(100.0)
        advanceUntilIdle() // 👈 dejamos que el primer pago termine completo

        viewModel.pay(100.0) // ahora sí, un segundo intento después de ya haber pagado
        advanceUntilIdle()

        // solo debe haberse llamado UNA vez, no dos
        coVerify(exactly = 1) { orderRepository.createOrder(any(), any(), any()) }
    }
}