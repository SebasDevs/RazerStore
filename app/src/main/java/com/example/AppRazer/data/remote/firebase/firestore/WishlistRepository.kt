package com.example.AppRazer.data.remote.firebase.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class WishlistItem(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val priceValue: Double = 0.0,
    val imageUrl: String = ""
)

@Singleton
class WishlistRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid

    private fun wishlistRef() = userId?.let {
        db.collection("users").document(it).collection("wishlist")
    }

    suspend fun addItem(item: WishlistItem): Result<Unit> {
        return try {
            wishlistRef()?.document(item.id)?.set(
                mapOf(
                    "id" to item.id,
                    "name" to item.name,
                    "price" to item.price,
                    "priceValue" to item.priceValue,
                    "imageUrl" to item.imageUrl
                )
            )?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeItem(itemId: String): Result<Unit> {
        return try {
            wishlistRef()?.document(itemId)?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWishlist(): Result<List<WishlistItem>> {
        return try {
            val snapshot = wishlistRef()?.get()?.await()
            val items = snapshot?.documents?.mapNotNull { doc ->
                WishlistItem(
                    id = doc.getString("id") ?: return@mapNotNull null,
                    name = doc.getString("name") ?: "",
                    price = doc.getString("price") ?: "",
                    priceValue = doc.getDouble("priceValue") ?: 0.0,
                    imageUrl = doc.getString("imageUrl") ?: ""
                )
            } ?: emptyList()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun mergeGuestWishlist(guestItems: List<WishlistItem>): Result<Unit> {
        return try {
            guestItems.forEach { item -> addItem(item) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}