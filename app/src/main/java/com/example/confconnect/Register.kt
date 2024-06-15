package com.example.confconnect

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.reference.child("users")

        // Set password input type to password
        binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD


        binding.alreadyRegistered.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.registerBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val name = binding.name.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Get the current user's UID
                            val currentUser = firebaseAuth.currentUser
                            val userId = currentUser?.uid

                            // Save email and username to Realtime Database under "users" node
                            userId?.let {
                                val userData = HashMap<String, Any>()
                                userData["email"] = email
                                userData["name"] = name
                                userData["id"] = userId

                                usersRef.child(it).setValue(userData)
                                    .addOnSuccessListener {
                                        val intent = Intent(this, Login::class.java)
                                        startActivity(intent)
                                        Toast.makeText(
                                            this,
                                            "User created and data saved",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Error saving user data: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Error creating user: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.ic_eye_off_line) // eye close
            } else {
                binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.ic_eye_line) // eye open
            }
            binding.password.text?.let { it1 -> binding.password.setSelection(it1.length) }  // Move cursor to the end
        }
    }
}
