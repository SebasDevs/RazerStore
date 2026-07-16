package com.example.AppRazer.data.remote.firebase.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class PaymentMethod(
    val id: String = "",
    val alias: String = "",
    val last4: String = "",
    val brand: String = "",
    val expiry: String = "",
    val isDefault: Boolean = false
)

@Singleton
class PaymentMethodRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid

    private fun ref() = userId?.let {
        db.collection("users").document(it).collection("paymentMethods")
    }

    suspend fun getPaymentMethods(): Result<List<PaymentMethod>> {
        return try {
            val snapshot = ref()?.get()?.await()
            val items = snapshot?.documents?.mapNotNull { doc ->
                PaymentMethod(
                    id = doc.getString("id") ?: return@mapNotNull null,
                    alias = doc.getString("alias") ?: "",
                    last4 = doc.getString("last4") ?: "",
                    brand = doc.getString("brand") ?: "",
                    expiry = doc.getString("expiry") ?: "",
                    isDefault = doc.getBoolean("isDefault") ?: false
                )
            } ?: emptyList()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun savePaymentMethod(method: PaymentMethod): Result<Unit> {
        return try {
            val id = method.id.ifEmpty { UUID.randomUUID().toString() }
            val toSave = method.copy(id = id)

            if (toSave.isDefault) {
                val current = ref()?.get()?.await()
                current?.documents?.forEach { doc ->
                    if (doc.id != id) {
                        ref()?.document(doc.id)?.update("isDefault", false)?.await()
                    }
                }
            }

            ref()?.document(id)?.set(
                mapOf(
                    "id" to toSave.id,
                    "alias" to toSave.alias,
                    "last4" to toSave.last4,
                    "brand" to toSave.brand,
                    "expiry" to toSave.expiry,
                    "isDefault" to toSave.isDefault
                )
            )?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePaymentMethod(methodId: String): Result<Unit> {
        return try {
            ref()?.document(methodId)?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}