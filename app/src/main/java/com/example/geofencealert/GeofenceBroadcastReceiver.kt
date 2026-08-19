package com.example.geofencealert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
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

        // Placeholder: Geofence transition handling will be implemented in C3.
    }
}
