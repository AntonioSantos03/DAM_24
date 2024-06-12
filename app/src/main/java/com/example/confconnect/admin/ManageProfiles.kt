package com.example.confconnect.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.Login
import com.example.confconnect.databinding.ActivityManageProfilesBinding
import com.google.firebase.auth.FirebaseAuth

class ManageProfiles : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityManageProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is authenticated
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // If user is not authenticated, redirect to login screen
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        // Set up UI elements and user management functionality
        // For example, display users in a list, implement actions to delete or manage users, etc.
        // You can use Firebase Authentication APIs to retrieve user data and perform actions.
    }
}
