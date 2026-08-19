package com.example.geofencealert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // C2: Read GeofencingEvent from the incoming Intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        // C2: Check for errors in the GeofencingEvent
        if (geofencingEvent == null || geofencingEvent.hasError()) {
            val errorCode = geofencingEvent?.errorCode ?: -1
            Log.e(TAG, "GeofencingEvent error. Error code: $errorCode")
            return
        }

        // C3: Determine the type of geofence transition
        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER &&
            geofenceTransition != Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.e(TAG, "Unknown geofence transition type: $geofenceTransition")
            return
        }

        // C3: Obtain the triggering geofences from the event
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        // Placeholder: Triggered transition response will be implemented in C4.
    }
}
