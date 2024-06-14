package com.example.confconnect

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var database: FirebaseDatabase
    private lateinit var locationsRef: DatabaseReference

    private var locationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_map)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        locationsRef = database.reference.child("locations")

        // Retrieve location id from intent extras
        locationId = intent.getStringExtra("locationId")

        // Check if locationId is provided
        if (locationId.isNullOrEmpty()) {
            Log.e("ShowMapActivity", "Error: Location ID not provided")
            Toast.makeText(this, "Error: Location ID not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Fetch location details from Firebase
        locationsRef.child(locationId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val location = snapshot.getValue(Location::class.java)
                if (location != null) {
                    val coordinates = location.coordinates
                    val locationName = location.name

                    // Display location on map
                    displayLocationOnMap(coordinates, locationName)
                } else {
                    Toast.makeText(applicationContext, "Location not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ShowMapActivity", "Failed to retrieve location: ${error.message}")
                Toast.makeText(applicationContext, "Failed to retrieve location", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun displayLocationOnMap(coordinates: String?, locationName: String?) {
        val latLng = parseCoordinates(coordinates)

        if (latLng != null) {
            val marker = MarkerOptions().position(latLng).title(locationName)
            mMap.addMarker(marker)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        } else {
            Toast.makeText(this, "Invalid location coordinates", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun parseCoordinates(coordinates: String?): LatLng? {
        val parts = coordinates?.split(",")?.map { it.trim() } // Trim spaces around coordinates
        return if (parts?.size == 2) {
            try {
                val lat = parts[0].toDouble()
                val lng = parts[1].toDouble()
                LatLng(lat, lng)
            } catch (e: NumberFormatException) {
                null
            }
        } else {
            null
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}

