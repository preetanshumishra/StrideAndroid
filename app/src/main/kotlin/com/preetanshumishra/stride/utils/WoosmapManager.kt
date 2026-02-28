package com.preetanshumishra.stride.utils

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.services.NearbyService
import com.preetanshumishra.stride.services.PlaceService
import com.webgeoservices.woosmapgeofencing.Woosmap
import com.webgeoservices.woosmapgeofencing.WoosmapSettings
import com.webgeoservices.woosmapgeofencingcore.WoosmapProvider
import com.webgeoservices.woosmapgeofencingcore.database.RegionLog
import com.webgeoservices.woosmapgeofencingcore.database.Visit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class WoosmapManager(
    private val context: Context,
    private val placeService: PlaceService,
    private val nearbyService: NearbyService,
    private val locationHelper: LocationHelper
) {
    private val woosmap: Woosmap = Woosmap.getInstance().initializeWoosmap(context)
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var savedPlaces: List<Place> = emptyList()

    init {
        WoosmapSettings.privateKeyWoosmapAPI = ""
        woosmap.setVisitReadyListener(VisitListener())
        woosmap.setRegionLogReadyListener(RegionLogListener())
        woosmap.startTracking(Woosmap.ConfigurationProfile.visitsTracking)
    }

    fun onResume() = woosmap.onResume()

    fun onPause() = woosmap.onPause()

    fun registerGeofences(places: List<Place>) {
        savedPlaces = places
        places.forEach { place ->
            Woosmap.getInstance().addGeofence(
                "place_${place.id}",
                LatLng(place.latitude, place.longitude),
                100f,
                "circle"
            )
        }
    }

    private fun handleVisit(lat: Double, lng: Double) {
        val nearest = findNearestPlace(lat, lng, 0.1) ?: return
        coroutineScope.launch {
            placeService.recordVisit(nearest.id)
        }
    }

    private suspend fun handleGeofenceEnter(identifier: String, lat: Double, lng: Double) {
        if (!identifier.startsWith("place_")) return
        val placeId = identifier.removePrefix("place_")
        val place = savedPlaces.firstOrNull { it.id == placeId } ?: return
        val errandCount = try {
            val result = nearbyService.getNearby(lat, lng)
            if (result is Resource.Success) result.data?.linkedErrands?.size ?: 0 else 0
        } catch (e: Exception) {
            0
        }
        showNotification(place.name, errandCount)
    }

    private fun findNearestPlace(lat: Double, lng: Double, thresholdKm: Double): Place? {
        return savedPlaces
            .filter { haversineKm(lat, lng, it.latitude, it.longitude) < thresholdKm }
            .minByOrNull { haversineKm(lat, lng, it.latitude, it.longitude) }
    }

    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return R * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    private fun showNotification(placeName: String, errandCount: Int) {
        val text = if (errandCount > 0)
            "You have $errandCount pending errand${if (errandCount == 1) "" else "s"} here"
        else
            "You're near one of your saved places"
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("Near $placeName")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    inner class VisitListener : WoosmapProvider.VisitReadyListener {
        override fun VisitReadyCallback(visit: Visit) {
            coroutineScope.launch { handleVisit(visit.lat, visit.lng) }
        }
    }

    inner class RegionLogListener : WoosmapProvider.RegionLogReadyListener {
        override fun RegionLogReadyCallback(regionLog: RegionLog) {
            if (regionLog.didEnter) {
                coroutineScope.launch { handleGeofenceEnter(regionLog.identifier, regionLog.lat, regionLog.lng) }
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "stride_geofence"
    }
}
