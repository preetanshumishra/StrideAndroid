package com.preetanshumishra.stride.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationHelper(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location {
        val cancellationToken = CancellationTokenSource()
        return suspendCancellableCoroutine { cont ->
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(location)
                    } else {
                        cont.resumeWithException(Exception("Location unavailable"))
                    }
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
            cont.invokeOnCancellation { cancellationToken.cancel() }
        }
    }
}
