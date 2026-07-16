package com.example.AppRazer.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.AppRazer.data.remote.firebase.firestore.WishlistItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private val Context.guestWishlistDataStore by preferencesDataStore(name = "guest_wishlist")

@Singleton
class GuestWishlistRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val wishlistKey = stringPreferencesKey("guest_wishlist_items")

    suspend fun getWishlist(): List<WishlistItem> {
        val json = context.guestWishlistDataStore.data.first()[wishlistKey] ?: return emptyList()
        return parseWishlistJson(json)
    }

    suspend fun saveWishlist(items: List<WishlistItem>) {
        context.guestWishlistDataStore.edit { prefs ->
            prefs[wishlistKey] = toWishlistJson(items)
        }
    }

    suspend fun clearWishlist() {
        context.guestWishlistDataStore.edit { prefs ->
            prefs.remove(wishlistKey)
        }
    }

    private fun toWishlistJson(items: List<WishlistItem>): String {
        val array = JSONArray()
        items.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("name", item.name)
            obj.put("price", item.price)
            obj.put("priceValue", item.priceValue)
            obj.put("imageUrl", item.imageUrl)
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseWishlistJson(json: String): List<WishlistItem> {
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                WishlistItem(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    price = obj.getString("price"),
                    priceValue = obj.getDouble("priceValue"),
                    imageUrl = obj.getString("imageUrl")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}