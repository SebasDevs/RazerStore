package com.example.AppRazer.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.appSettingsDataStore by preferencesDataStore(name = "app_settings")

@Singleton
class AppSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fontScaleKey = floatPreferencesKey("font_scale")
    private val orderUpdatesKey = booleanPreferencesKey("notif_order_updates")
    private val promotionsKey = booleanPreferencesKey("notif_promotions")
    private val productNewsKey = booleanPreferencesKey("notif_product_news")

    val fontScaleFlow = context.appSettingsDataStore.data.map { prefs ->
        prefs[fontScaleKey] ?: 1.0f
    }

    val orderUpdatesFlow = context.appSettingsDataStore.data.map { prefs ->
        prefs[orderUpdatesKey] ?: true
    }

    val promotionsFlow = context.appSettingsDataStore.data.map { prefs ->
        prefs[promotionsKey] ?: true
    }

    val productNewsFlow = context.appSettingsDataStore.data.map { prefs ->
        prefs[productNewsKey] ?: false
    }

    suspend fun setFontScale(scale: Float) {
        context.appSettingsDataStore.edit { prefs -> prefs[fontScaleKey] = scale }
    }

    suspend fun setOrderUpdates(enabled: Boolean) {
        context.appSettingsDataStore.edit { prefs -> prefs[orderUpdatesKey] = enabled }
    }

    suspend fun setPromotions(enabled: Boolean) {
        context.appSettingsDataStore.edit { prefs -> prefs[promotionsKey] = enabled }
    }

    suspend fun setProductNews(enabled: Boolean) {
        context.appSettingsDataStore.edit { prefs -> prefs[productNewsKey] = enabled }
    }
}