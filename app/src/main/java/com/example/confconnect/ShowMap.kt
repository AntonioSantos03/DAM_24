package com.example.confconnect

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ShowMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var firestore: FirebaseFirestore

    private var locationId: String? = null
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_map)

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Retrieve location id from intent extras
        locationId = intent.getStringExtra("locationId")
        Log.d("ShowMap", "locationId: $locationId")

        // Initialize MapView
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val btnBack: FloatingActionButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Set a temporary default location (e.g., Lisbon, Portugal)
        val defaultLocation = LatLng(38.732462, -9.159921)
        mMap.addMarker(MarkerOptions().position(defaultLocation).title("Default Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

        // Check if locationId is properly initialized
        if (locationId.isNullOrEmpty()) {
            Log.e("ShowMap", "locationId is null or empty")
            Toast.makeText(this, "Location ID is missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch specific location details from Firestore
        listenerRegistration = firestore.collection("locations").document(locationId!!)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ShowMap", "Failed to read value.", error)
                    Toast.makeText(this@ShowMap, "Failed to read value.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val location = snapshot.toObject(Location::class.java)
                    location?.let {
                        val coordinatesStr = it.coordinates
                        if (coordinatesStr != null && coordinatesStr.isNotBlank()) {
                            val coordinates = coordinatesStr.split(", ")
                            if (coordinates.size == 2) {
                                try {
                                    val lat = coordinates[0].toDouble()
                                    val lng = coordinates[1].toDouble()
                                    val locationLatLng = LatLng(lat, lng)
                                    mMap.clear() // Clear existing markers
                                    mMap.addMarker(MarkerOptions().position(locationLatLng).title(it.name))
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 16f))
                                } catch (e: NumberFormatException) {
                                    Log.e("ShowMap", "Error parsing coordinates: ${e.message}")
                                    Toast.makeText(this@ShowMap, "Error parsing coordinates", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Log.e("ShowMap", "Invalid coordinates format: $coordinatesStr")
                                Toast.makeText(this@ShowMap, "Invalid coordinates format", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e("ShowMap", "Coordinates field is null or empty")
                            Toast.makeText(this@ShowMap, "Coordinates not found", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Log.e("ShowMap", "Location is null")
                        Toast.makeText(this@ShowMap, "Location not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("ShowMap", "Snapshot is null or doesn't exist")
                    Toast.makeText(this@ShowMap, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        listenerRegistration?.remove()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}

