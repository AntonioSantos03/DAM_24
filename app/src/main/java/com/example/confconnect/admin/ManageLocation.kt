package com.example.confconnect.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.Location
import com.example.confconnect.R
import com.google.firebase.database.*

class ManageLocation : AppCompatActivity() {

    private lateinit var editTextLocationName: EditText
    private lateinit var editTextLocationAddress: EditText
    private lateinit var btnSaveLocation: Button
    private lateinit var btnPreviewLocation: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var locationsRef: DatabaseReference
    private lateinit var locationListener: ValueEventListener
    private var savedLocationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_location)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        locationsRef = database.reference.child("locations")

        // Initialize views
        editTextLocationName = findViewById(R.id.editTextLocationName)
        editTextLocationAddress = findViewById(R.id.editTextLocationAddress)
        btnSaveLocation = findViewById(R.id.btnSaveLocation)
        btnPreviewLocation = findViewById(R.id.btnPreviewLocation)

        // Retrieve last saved location if any
        retrieveLastSavedLocation()

        //back btn
        val backBtn = findViewById<Button>(R.id.btnBack)
        backBtn.setOnClickListener {
            finish()
        }

        // Save location button click listener
        btnSaveLocation.setOnClickListener { saveLocation() }

        // Optional: Preview location button click listener
        btnPreviewLocation.setOnClickListener {
            // Implement preview location functionality if needed
            Toast.makeText(this, "Preview Location button clicked", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove ValueEventListener when activity is destroyed to prevent memory leaks
        locationsRef.removeEventListener(locationListener)
    }

    private fun retrieveLastSavedLocation() {
        // Query to get the last saved location
        val query = locationsRef.orderByKey().limitToLast(1)
        locationListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val location = data.getValue(Location::class.java)
                    location?.let {
                        savedLocationId = data.key
                        editTextLocationAddress.hint = it.coordinates ?: ""
                        editTextLocationName.hint = it.name ?: ""
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(this@ManageLocation, "Failed to retrieve last saved location", Toast.LENGTH_SHORT).show()
            }
        }
        query.addValueEventListener(locationListener)
    }

    private fun saveLocation() {
        val locationName = editTextLocationName.text.toString().trim()
        val locationCoordinates = editTextLocationAddress.text.toString().trim()

        // Validate input
        if (locationName.isEmpty() || locationCoordinates.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if a location is already saved
        if (savedLocationId != null) {
            // Update existing location
            val location = Location(savedLocationId, locationCoordinates, locationName)
            locationsRef.child(savedLocationId!!).setValue(location)
                .addOnSuccessListener {
                    Toast.makeText(this, "Location updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Optionally finish activity after saving
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update location: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Create new location
            val locationId = locationsRef.push().key // Generate unique key for location
            val location = Location(locationId, locationCoordinates, locationName)
            locationId?.let {
                locationsRef.child(it).setValue(location)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Location saved successfully", Toast.LENGTH_SHORT).show()
                        finish() // Optionally finish activity after saving
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save location: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
