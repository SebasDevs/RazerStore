package com.example.AppRazer.data.remote.firebase.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class Address(
    val id: String = "",
    val label: String = "",
    val street: String = "",
    val city: String = "",
    val province: String = "",
    val zipCode: String = "",
    val phone: String = "",
    val isDefault: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Singleton
class AddressRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid

    private fun addressesRef() = userId?.let {
        db.collection("users").document(it).collection("addresses")
    }

    suspend fun getAddresses(): Result<List<Address>> {
        return try {
            val snapshot = addressesRef()?.get()?.await()
            val items = snapshot?.documents?.mapNotNull { doc ->
                Address(
                    id = doc.getString("id") ?: return@mapNotNull null,
                    label = doc.getString("label") ?: "",
                    street = doc.getString("street") ?: "",
                    city = doc.getString("city") ?: "",
                    province = doc.getString("province") ?: "",
                    zipCode = doc.getString("zipCode") ?: "",
                    phone = doc.getString("phone") ?: "",
                    isDefault = doc.getBoolean("isDefault") ?: false,
                    latitude = doc.getDouble("latitude"),
                    longitude = doc.getDouble("longitude")
                )
            } ?: emptyList()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveAddress(address: Address): Result<Unit> {
        return try {
            val id = address.id.ifEmpty { UUID.randomUUID().toString() }
            val toSave = address.copy(id = id)

            // ── Si se marca como predeterminada, desmarca las demás ──
            if (toSave.isDefault) {
                val current = addressesRef()?.get()?.await()
                current?.documents?.forEach { doc ->
                    if (doc.id != id) {
                        addressesRef()?.document(doc.id)?.update("isDefault", false)?.await()
                    }
                }
            }

            addressesRef()?.document(id)?.set(
                mapOf(
                    "id" to toSave.id,
                    "label" to toSave.label,
                    "street" to toSave.street,
                    "city" to toSave.city,
                    "province" to toSave.province,
                    "zipCode" to toSave.zipCode,
                    "phone" to toSave.phone,
                    "isDefault" to toSave.isDefault,
                    "latitude" to toSave.latitude,
                    "longitude" to toSave.longitude
                )
            )?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAddress(addressId: String): Result<Unit> {
        return try {
            addressesRef()?.document(addressId)?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}