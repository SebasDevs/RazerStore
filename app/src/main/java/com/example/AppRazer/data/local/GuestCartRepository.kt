package com.example.AppRazer.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.AppRazer.presentation.screens.cart.CartItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private val Context.guestCartDataStore by preferencesDataStore(name = "guest_cart")

@Singleton
class GuestCartRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cartKey = stringPreferencesKey("guest_cart_items")

    suspend fun getCart(): List<CartItem> {
        val json = context.guestCartDataStore.data.first()[cartKey] ?: return emptyList()
        return parseCartJson(json)
    }

    suspend fun saveCart(items: List<CartItem>) {
        context.guestCartDataStore.edit { prefs ->
            prefs[cartKey] = toCartJson(items)
        }
    }

    suspend fun clearCart() {
        context.guestCartDataStore.edit { prefs ->
            prefs.remove(cartKey)
        }
    }

    private fun toCartJson(items: List<CartItem>): String {
        val array = JSONArray()
        items.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("name", item.name)
            obj.put("price", item.price)
            obj.put("imageUrl", item.imageUrl)
            obj.put("quantity", item.quantity)
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseCartJson(json: String): List<CartItem> {
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                CartItem(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    price = obj.getDouble("price"),
                    imageUrl = obj.getString("imageUrl"),
                    quantity = obj.getInt("quantity")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}