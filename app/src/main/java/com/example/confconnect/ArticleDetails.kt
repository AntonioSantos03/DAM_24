package com.example.confconnect

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.adapter.CommentAdapter
import com.example.confconnect.databinding.ActivityArticleDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ArticleDetails : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var commentsRef: DatabaseReference
    private lateinit var commentAdapter: CommentAdapter
    private var articleId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        commentsRef = database.getReference("comments")

        // Retrieve article details from intent
        val title = intent.getStringExtra("title")
        val author = intent.getStringExtra("author")
        val date = intent.getStringExtra("date")
        val description = intent.getStringExtra("description")
        val room = intent.getStringExtra("room")
        val time = intent.getStringExtra("time")
        articleId = intent.getStringExtra("articleId") ?: ""

        // Display article details
        binding.tvTitle.text = title
        binding.tvAuthor.text = author
        binding.tvDate.text = date
        binding.tvDescription.text = description
        binding.tvRoom.text = room
        binding.tvTime.text = time

        // Set up RecyclerView for comments
        binding.rvComments.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(emptyList())
        binding.rvComments.adapter = commentAdapter

        // Set up click listener for Submit button
        binding.btnSubmitComment.setOnClickListener {
            handleSubmitComment()
        }

        // Set up click listener for Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        if (articleId.isEmpty()) {
            Toast.makeText(this, "Invalid article ID", Toast.LENGTH_SHORT).show()
        } else {
            loadComments()
        }
    }

    private fun handleSubmitComment() {
        val commentText = binding.etComment.text.toString().trim()

        if (commentText.isNotEmpty()) {
            // Get current user from Firebase Authentication
            val currentUser = FirebaseAuth.getInstance().currentUser

            currentUser?.let { user ->
                // Retrieve user details from Firebase Database under "users" node
                val usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)

                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userId = user.uid
                        val userName = snapshot.child("name").getValue(String::class.java) ?: "Anonymous" // Default to "Anonymous" if name not found

                        // Create comment object
                        val timestamp = System.currentTimeMillis()
                        val commentId = commentsRef.push().key ?: ""

                        val comment = Comments(commentId, articleId, userId, userName, commentText, timestamp)

                        // Save comment to Realtime Database
                        commentsRef.child(commentId).setValue(comment)
                            .addOnSuccessListener {
                                Toast.makeText(this@ArticleDetails, "Comment added successfully", Toast.LENGTH_SHORT).show()
                                binding.etComment.text.clear()
                                loadComments() // Reload comments after adding new comment
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@ArticleDetails, "Failed to add comment: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ArticleDetails, "Failed to retrieve user data: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadComments() {
        if (articleId.isNotEmpty()) {
            commentsRef.orderByChild("articleId").equalTo(articleId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val comments = mutableListOf<Comments>()
                        for (commentSnapshot in snapshot.children) {
                            val comment = commentSnapshot.getValue(Comments::class.java)
                            comment?.let {
                                if (it.approved == true) { // Filter approved comments
                                    comments.add(it)
                                }
                            }
                        }
                        commentAdapter.setComments(comments)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ArticleDetails, "Failed to load comments: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this, "Invalid article ID", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Clean up listeners or resources if needed
    }
}
