package com.example.geofencealert

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var startGeofenceButton: Button
    private lateinit var stopGeofenceButton: Button
    private lateinit var eventLogText: TextView
    private lateinit var geofenceStatusText: TextView

    // Geofencing Client (for Member B to use)
    private lateinit var geofencingClient: GeofencingClient

    // C5: Receiver that listens for geofence event broadcasts from GeofenceBroadcastReceiver
    private val geofenceEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val logEntry = intent.getStringExtra(GeofenceBroadcastReceiver.EXTRA_EVENT_MESSAGE)
                ?: return
            // Prepend the new entry so the latest event appears at the top
            val currentLog = eventLogText.text.toString()
            val updatedLog = if (currentLog == "📋 Events will appear here...") {
                logEntry
            } else {
                "$logEntry\n$currentLog"
            }
            eventLogText.text = updatedLog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI Elements
        startGeofenceButton = findViewById(R.id.start_geofence_button)
        stopGeofenceButton = findViewById(R.id.stop_geofence_button)
        eventLogText = findViewById(R.id.event_log_text)
        geofenceStatusText = findViewById(R.id.geofence_status_text)

        // Initialize Geofencing Client (Member B will use this)
        geofencingClient = LocationServices.getGeofencingClient(this)

        // Set Click Listeners
        startGeofenceButton.setOnClickListener {
            Toast.makeText(this, "Start Geofence Button Clicked!", Toast.LENGTH_SHORT).show()
            checkAndRequestPermissions()
        }

        stopGeofenceButton.setOnClickListener {
            Toast.makeText(this, "Stop Geofence Button Clicked!", Toast.LENGTH_SHORT).show()
            eventLogText.text = "🛑 Stop button clicked - geofence will be removed"
        }
    }

    // C5: Register receiver when Activity is active
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT)
        registerReceiver(geofenceEventReceiver, filter)
    }

    // C5: Unregister receiver when Activity is paused to avoid leaks
    override fun onPause() {
        super.onPause()
        unregisterReceiver(geofenceEventReceiver)
    }

    // ⭐⭐⭐ YOUR MAIN JOB: PERMISSION HANDLING ⭐⭐⭐

    // Function to check and request permissions
    private fun checkAndRequestPermissions(): Boolean {
        // 1. Check Foreground Permissions (Fine and Coarse)
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted || !coarseLocationGranted) {
            // Request Foreground Permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                FOREGROUND_PERMISSION_REQUEST_CODE
            )
            return false
        }

        // 2. Check Background Permission (Android 10+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val backgroundLocationGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!backgroundLocationGranted) {
                // Request Background Permission SEPARATELY
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    BACKGROUND_PERMISSION_REQUEST_CODE
                )
                return false
            }
        }

        eventLogText.text = "✅ Permissions already granted! Ready for geofence."
        return true
    }

    // Handle the user's response to permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            FOREGROUND_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    eventLogText.text = "✅ Foreground location granted! Now requesting background..."
                    // After foreground is granted, request background
                    checkAndRequestPermissions()
                } else {
                    eventLogText.text = "❌ Foreground permission denied!"
                    Toast.makeText(this, "❌ Permission Denied!", Toast.LENGTH_LONG).show()
                }
            }
            BACKGROUND_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    eventLogText.text = "✅ All permissions granted! Ready to start."
                    Toast.makeText(this, "✅ Permissions Granted!", Toast.LENGTH_SHORT).show()
                } else {
                    eventLogText.text = "❌ Background permission denied! Geofencing needs 'Allow all the time'."
                    Toast.makeText(this, "❌ Background Permission Denied!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private const val FOREGROUND_PERMISSION_REQUEST_CODE = 1001
        private const val BACKGROUND_PERMISSION_REQUEST_CODE = 1002
    }
}