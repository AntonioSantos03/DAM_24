package com.example.confconnect.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.Location
import com.example.confconnect.R
import com.example.confconnect.ShowMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class ManageLocation : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var btnSaveLocation: Button
    private lateinit var btnPreviewLocation: Button
    private lateinit var btnBack: FloatingActionButton

    private lateinit var database: FirebaseDatabase
    private lateinit var locationsRef: DatabaseReference
    private var savedLocationId: String? = null

    private var selectedLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_location)

        database = FirebaseDatabase.getInstance()
        locationsRef = database.reference.child("locations")

        btnSaveLocation = findViewById(R.id.btnSaveLocation)
        btnPreviewLocation = findViewById(R.id.btnPreviewLocation)
        btnBack = findViewById(R.id.btnBack)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnBack.setOnClickListener {
            finish()
        }

        btnSaveLocation.setOnClickListener {
            saveLocation()
        }

        btnPreviewLocation.setOnClickListener {
            val intent = Intent(this, ShowMap::class.java)
            intent.putExtra("locationId", savedLocationId)
            intent.putExtra("coordinates", selectedLocation?.latitude.toString() + "," + selectedLocation?.longitude)

            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()

            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))

            selectedLocation = latLng
        }

        loadSavedLocation()
    }

    private fun loadSavedLocation() {
        locationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val location = data.getValue(Location::class.java)
                    location?.let {
                        savedLocationId = data.key
                        val coordinatesStr = it.coordinates
                        if (coordinatesStr != null && coordinatesStr.isNotBlank()) {
                            val coordinates = coordinatesStr.split(",")
                            if (coordinates.size == 2) {
                                try {
                                    val lat = coordinates[0].toDouble()
                                    val lng = coordinates[1].toDouble()
                                    val locationLatLng = LatLng(lat, lng)
                                    mMap.addMarker(MarkerOptions().position(locationLatLng).title(it.name))
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 13f))
                                } catch (e: NumberFormatException) {
                                    Toast.makeText(this@ManageLocation, "Error parsing coordinates", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@ManageLocation, "Invalid coordinates format", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@ManageLocation, "Coordinates not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    break
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageLocation, "Failed to retrieve saved location", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveLocation() {
        if (selectedLocation == null) {
            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show()
            return
        }

        val locationName = "Saved Location"
        val locationCoordinates = "${selectedLocation!!.latitude},${selectedLocation!!.longitude}"
        Log.e("ManageLocation", "Location coordinates: $locationCoordinates")

        val locationId = savedLocationId ?: locationsRef.push().key ?: return
        val location = Location(locationId, locationCoordinates, locationName)

        locationsRef.child(locationId).setValue(location)
            .addOnSuccessListener {
                Toast.makeText(this, "Location saved successfully", Toast.LENGTH_SHORT).show()
                savedLocationId = locationId
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(selectedLocation!!).title(locationName))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation!!, 16f))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save location: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
