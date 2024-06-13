package com.example.confconnect.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.databinding.ActivityEditUserBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditUser : AppCompatActivity() {

    private lateinit var binding: ActivityEditUserBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("userId") ?: ""

        // Set up user details
        binding.tvUserEmail.text = intent.getStringExtra("userEmail")

        binding.btnSave.setOnClickListener {
            // Implement save functionality if needed
            Toast.makeText(this, "Save functionality not implemented", Toast.LENGTH_SHORT).show()
        }

        binding.btnDelete.setOnClickListener {
            deleteUser()
        }
    }

    private fun deleteUser() {
        firestore.collection("users").document(userId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show()
            }
    }
}
