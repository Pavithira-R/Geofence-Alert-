package com.example.geofencealert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceReceiver"
        const val ACTION_GEOFENCE_EVENT = "com.example.geofencealert.GEOFENCE_EVENT"
        const val EXTRA_EVENT_MESSAGE = "event_message"
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

        // C4: Show a Toast notification for the geofence transition
        val message = when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "Entered Geofence!"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "Exited Geofence!"
            else -> return
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        // C5: Build a timestamped entry and broadcast it to MainActivity
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        val logEntry = "[$timestamp] $message"

        val eventIntent = Intent(ACTION_GEOFENCE_EVENT).apply {
            putExtra(EXTRA_EVENT_MESSAGE, logEntry)
        }
        context.sendBroadcast(eventIntent)
    }
}
