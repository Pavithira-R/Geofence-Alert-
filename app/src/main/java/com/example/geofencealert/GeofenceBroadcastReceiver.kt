package com.example.geofencealert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Get the geofencing event from the intent
        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) } ?: return

        // Check if there was an error
        if (geofencingEvent.hasError()) {
            Toast.makeText(context, "Geofence Error!", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the transition type (ENTER or EXIT)
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Get the geofences that triggered this event
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        // Process each triggered geofence
        triggeringGeofences?.forEach { geofence ->
            // Determine what type of transition occurred
            val transitionType = when (geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTERED 🟢"
                Geofence.GEOFENCE_TRANSITION_EXIT -> "EXITED 🔴"
                else -> "UNKNOWN"
            }

            // Show a toast notification
            Toast.makeText(
                context,
                "Geofence ${geofence.requestId}: $transitionType",
                Toast.LENGTH_LONG
            ).show()

            // You can also show a notification or update UI here
            // Member C will expand this further
        }
    }
}
