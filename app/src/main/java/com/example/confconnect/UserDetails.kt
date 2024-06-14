package com.example.confconnect

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.adapter.CommentAdapter
import com.example.confconnect.databinding.ActivityUserDetailsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import kotlin.math.log

class UserDetails : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var commentsRef: DatabaseReference
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var userId: String
    private lateinit var userEmail: String
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        commentsRef = database.getReference("comments")

        // Get user details from intent
        userId = intent.getStringExtra("userId") ?: ""
        userEmail = intent.getStringExtra("userEmail") ?: ""

        // Initialize RecyclerView for comments
        binding.rvUserComments.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(emptyList())
        binding.rvUserComments.adapter = commentAdapter

        // Load user details from database
        loadUserName()

        // Set up click listener for Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun loadUserName() {
        val usersRef = database.getReference("users").child(userId)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    binding.tvUserName.text = userName
                    binding.tvUserEmail.text = "Email: $userEmail"

                    // Load user's comments
                    loadUserComments()
                } else {
                    binding.tvUserName.text = "User Name: Unknown"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserDetails, "Failed to load user name: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadUserComments() {
        commentsRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val comments = mutableListOf<Comments>()
                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comments::class.java)
                        comment?.let { comments.add(it) }
                    }

                    // Clear the adapter first
                    commentAdapter.clearComments()

                    // If there are comments, add them to the adapter
                    if (comments.isNotEmpty()) {
                        commentAdapter.setComments(comments)
                    } else {
                        // Handle case where there are no comments for this user
                        Toast.makeText(this@UserDetails, "No questions found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserDetails, "Failed to load questions: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


}
