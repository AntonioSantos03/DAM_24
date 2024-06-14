package com.example.confconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.confconnect.admin.AdminPage
import com.example.confconnect.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        // Set password input type to password
        binding.password.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        binding.notRegistered.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        binding.loginBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (email == "admin@admin.com") {
                            val intent = Intent(this, AdminPage::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Error: ${task.exception?.message}",
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
                binding.password.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.ic_eye_line) //eye open
            } else {
                binding.password.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.ic_eye_off_line) //eye close
            }
            binding.password.text?.let { it1 -> binding.password.setSelection(it1.length) }  // Move cursor to the end
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // User is signed in
            val email = currentUser.email
            if (email != null && email == "admin@admin.com") {
                // Redirect to admin page
                val intent = Intent(this, AdminPage::class.java)
                startActivity(intent)
                finish() // Finish the current activity to prevent going back
            } else {
                // Redirect to main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Finish the current activity to prevent going back
            }
        }
    }
}
