package com.example.AppRazer.presentation.components

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class GeocodedAddress(
    val street: String = "",
    val city: String = "",
    val province: String = "",
    val zipCode: String = ""
)

object LocationHelper {

    suspend fun getCurrentLocation(context: Context): Location? =
        suspendCancellableCoroutine { cont ->
            try {
                val client = LocationServices.getFusedLocationProviderClient(context)
                client.lastLocation
                    .addOnSuccessListener { location -> cont.resume(location) }
                    .addOnFailureListener { cont.resume(null) }
            } catch (e: SecurityException) {
                cont.resume(null)
            }
        }

    @Suppress("DEPRECATION")
    fun reverseGeocode(context: Context, lat: Double, lng: Double): GeocodedAddress? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val results = geocoder.getFromLocation(lat, lng, 1)
            val address = results?.firstOrNull() ?: return null
            GeocodedAddress(
                street = listOfNotNull(address.thoroughfare, address.subThoroughfare)
                    .joinToString(" ").ifEmpty { address.getAddressLine(0) ?: "" },
                city = address.locality ?: address.subAdminArea ?: "",
                province = address.adminArea ?: "",
                zipCode = address.postalCode ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("DEPRECATION")
    fun forwardGeocode(context: Context, query: String): Location? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val results = geocoder.getFromLocationName(query, 1)
            val result = results?.firstOrNull() ?: return null
            Location("").apply {
                latitude = result.latitude
                longitude = result.longitude
            }
        } catch (e: Exception) {
            null
        }
    }
}