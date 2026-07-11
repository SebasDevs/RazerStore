package com.example.AppRazer.data.remote.firebase.firestore

import com.example.AppRazer.presentation.screens.cart.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

data class Order(
    val id: String = "",
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val status: String = "pending",
    val date: Long = Date().time,
    val paymentMethod: String = ""
)

@Singleton
class OrderRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid

    private fun ordersRef() = userId?.let {
        db.collection("users").document(it).collection("orders")
    }

    // ── Crear pedido ──────────────────────────────────────────────
    suspend fun createOrder(
        items: List<CartItem>,
        total: Double,
        paymentMethod: String
    ): Result<String> {
        return try {
            val orderRef = ordersRef()?.document()
            val orderId = orderRef?.id ?: return Result.failure(Exception("No user"))

            val itemsMap = items.map { item ->
                mapOf(
                    "id" to item.id,
                    "name" to item.name,
                    "price" to item.price,
                    "imageUrl" to item.imageUrl,
                    "quantity" to item.quantity
                )
            }

            orderRef.set(
                mapOf(
                    "id" to orderId,
                    "items" to itemsMap,
                    "total" to total,
                    "status" to "confirmed",
                    "date" to Date().time,
                    "paymentMethod" to paymentMethod
                )
            ).await()

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Obtener pedidos ───────────────────────────────────────────
    suspend fun getOrders(): Result<List<Order>> {
        return try {
            val snapshot = ordersRef()
                ?.orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                ?.get()?.await()

            val orders = snapshot?.documents?.mapNotNull { doc ->
                val itemsList = (doc.get("items") as? List<Map<String, Any>>)?.map { itemMap ->
                    CartItem(
                        id = itemMap["id"] as? String ?: "",
                        name = itemMap["name"] as? String ?: "",
                        price = (itemMap["price"] as? Double) ?: 0.0,
                        imageUrl = itemMap["imageUrl"] as? String ?: "",
                        quantity = (itemMap["quantity"] as? Long)?.toInt() ?: 1
                    )
                } ?: emptyList()

                Order(
                    id = doc.getString("id") ?: "",
                    items = itemsList,
                    total = doc.getDouble("total") ?: 0.0,
                    status = doc.getString("status") ?: "pending",
                    date = doc.getLong("date") ?: 0L,
                    paymentMethod = doc.getString("paymentMethod") ?: ""
                )
            } ?: emptyList()

            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}