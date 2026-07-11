package com.example.AppRazer.data.remote.firebase.firestore

import com.example.AppRazer.presentation.screens.cart.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid

    private fun cartRef() = userId?.let {
        db.collection("users").document(it).collection("cart")
    }

    // ── Guardar item en Firestore ─────────────────────────────────
    suspend fun addItem(item: CartItem): Result<Unit> {
        return try {
            cartRef()?.document(item.id)?.set(
                mapOf(
                    "id" to item.id,
                    "name" to item.name,
                    "price" to item.price,
                    "imageUrl" to item.imageUrl,
                    "quantity" to item.quantity
                )
            )?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Actualizar cantidad ───────────────────────────────────────
    suspend fun updateQuantity(itemId: String, quantity: Int): Result<Unit> {
        return try {
            cartRef()?.document(itemId)?.update("quantity", quantity)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Eliminar item ─────────────────────────────────────────────
    suspend fun removeItem(itemId: String): Result<Unit> {
        return try {
            cartRef()?.document(itemId)?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Obtener carrito ───────────────────────────────────────────
    suspend fun getCart(): Result<List<CartItem>> {
        return try {
            val snapshot = cartRef()?.get()?.await()
            val items = snapshot?.documents?.mapNotNull { doc ->
                CartItem(
                    id = doc.getString("id") ?: return@mapNotNull null,
                    name = doc.getString("name") ?: return@mapNotNull null,
                    price = doc.getDouble("price") ?: return@mapNotNull null,
                    imageUrl = doc.getString("imageUrl") ?: "",
                    quantity = (doc.getLong("quantity") ?: 1).toInt()
                )
            } ?: emptyList()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Vaciar carrito ────────────────────────────────────────────
    suspend fun clearCart(): Result<Unit> {
        return try {
            val snapshot = cartRef()?.get()?.await()
            snapshot?.documents?.forEach { it.reference.delete().await() }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}