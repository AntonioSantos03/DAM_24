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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class ShowMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: SupportMapFragment
    private lateinit var database: FirebaseDatabase
    private lateinit var locationsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_map)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        locationsRef = database.reference.child("locations")

        // Initialize MapView
        mapView = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapView.getMapAsync(this)

        // Back button
        val btnBack: FloatingActionButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Fetch location data from intent extras
        val locationId = intent.getStringExtra("locationId")

        if (locationId == null) {
            Log.e(TAG, "Location ID not found in intent extras")
            Toast.makeText(this, "Location ID not found", Toast.LENGTH_SHORT).show()
            finish() // Finish the activity if locationId is missing
            return
        }

        // Retrieve coordinates from Firebase Database
        locationsRef.child(locationId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val location = snapshot.getValue(Location::class.java)
                if (location != null) {
                    val coordinatesStr = location.coordinates
                    if (!coordinatesStr.isNullOrBlank()) {
                        val coordinates = coordinatesStr.split(",")
                        if (coordinates.size == 2) {
                            try {
                                val lat = coordinates[0].toDouble()
                                val lng = coordinates[1].toDouble()
                                val locationLatLng = LatLng(lat, lng)
                                mMap.clear() // Clear existing markers
                                mMap.addMarker(MarkerOptions().position(locationLatLng).title("Location"))
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 14f))
                            } catch (e: NumberFormatException) {
                                Log.e(TAG, "Error parsing coordinates: ${e.message}")
                                Toast.makeText(this@ShowMap, "Error parsing coordinates", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e(TAG, "Invalid coordinates format: $coordinatesStr")
                            Toast.makeText(this@ShowMap, "Invalid coordinates format", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e(TAG, "Coordinates field is null or empty")
                        Toast.makeText(this@ShowMap, "Coordinates not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "Location not found in database")
                    Toast.makeText(this@ShowMap, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve location from database: ${error.message}")
                Toast.makeText(this@ShowMap, "Failed to retrieve location", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val TAG = "ShowMap"
    }
}
