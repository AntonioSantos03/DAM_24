package com.example.confconnect

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.adapter.CommentAdapter
import com.example.confconnect.databinding.ActivityArticleDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ArticleDetails : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var commentAdapter: CommentAdapter
    private var articleId: String? = null // Assuming you have an article ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve article details from intent
        val title = intent.getStringExtra("title")
        val author = intent.getStringExtra("author")
        val date = intent.getStringExtra("date")
        val description = intent.getStringExtra("description")
        val room = intent.getStringExtra("room")
        articleId = intent.getStringExtra("id") // Assuming article ID is passed

        // Display article details
        binding.tvTitle.text = title
        binding.tvAuthor.text = author
        binding.tvDate.text = date
        binding.tvDescription.text = description
        binding.tvRoom.text = room


        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Set up RecyclerView for comments
        binding.rvComments.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(emptyList()) { comment ->
            // Handle item click if needed
        }
        binding.rvComments.adapter = commentAdapter

        // Set up click listener for Submit button
        binding.btnSubmitComment.setOnClickListener {
            handleSubmitComment()
        }

        // Set up click listener for Back button
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        loadComments()
    }

    private fun handleSubmitComment() {
        val commentText = binding.etComment.text.toString().trim()

        if (commentText.isNotEmpty() && articleId != null) {
            // Replace with actual user details retrieval logic
            val userId = "placeholder_user_id"
            val userName = "placeholder_user_name"

            // Create a new comment object with current timestamp
            val comment = Comment(
                commentId = "", // Firestore will generate a unique ID
                userId = userId,
                userName = userName,
                commentText = commentText,
                timestamp = null // Timestamp will be set by Firestore
            )

            // Add comment to Firestore
            firestore.collection("articles").document(articleId!!)
                .collection("comments").add(comment)
                .addOnSuccessListener {
                    // Comment added successfully
                    binding.etComment.text.clear()
                    loadComments() // Reload comments to display the new comment
                    Toast.makeText(this, "Comment added successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Error adding comment
                    Toast.makeText(this, "Failed to add comment: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadComments() {
        if (articleId != null) {
            firestore.collection("articles").document(articleId!!)
                .collection("comments").get()
                .addOnSuccessListener { result ->
                    val comments = parseComments(result)
                    commentAdapter.updateComments(comments)
                }
                .addOnFailureListener { e ->
                    // Error getting comments
                    Toast.makeText(this, "Failed to load comments: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Invalid article ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseComments(result: QuerySnapshot): List<Comment> {
        return result.documents.mapNotNull { document ->
            document.toObject(Comment::class.java)
        }
    }
}
