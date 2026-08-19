package com.example.geofencealert

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceHelper(private val context: Context) {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val TAG = "GeofenceHelper"

    // B1 — DEFINE GEOFENCE CONSTANTS
    companion object {
        const val LAT = 6.9271
        const val LNG = 79.8612
        const val RADIUS = 100f
        const val GEO_ID = "MY_GEOFENCE"
    }

    // B4 — CREATE THE PENDINGINTENT
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        // Use FLAG_UPDATE_CURRENT and FLAG_MUTABLE as required
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    // B2 — BUILD THE GEOFENCE OBJECT
    private fun createGeofence(): Geofence {
        return Geofence.Builder()
            .setRequestId(GEO_ID)
            .setCircularRegion(LAT, LNG, RADIUS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    // B3 — BUILD THE GEOFENCING REQUEST
    private fun createGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
    }

    // B5 — REGISTER THE GEOFENCE
    @SuppressLint("MissingPermission")
    fun registerGeofence() {
        val geofence = createGeofence()
        val geofencingRequest = createGeofencingRequest(geofence)

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "Geofence registered successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Geofence registration failed: ${exception.message}")
            }
    }

    // B6 — REMOVE GEOFENCE
    fun removeGeofence() {
        geofencingClient.removeGeofences(geofencePendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "Geofence removed successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Geofence removal failed: ${exception.message}")
            }
    }
}
