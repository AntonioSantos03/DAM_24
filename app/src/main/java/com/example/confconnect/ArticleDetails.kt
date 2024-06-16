package com.example.confconnect

import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.adapter.CommentAdapter
import com.example.confconnect.databinding.ActivityArticleDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File

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

        /* Set up click listener for Download button
        binding.btnDownload.setOnClickListener {
            generatePdf()
        } */

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
                                Toast.makeText(this@ArticleDetails, "Your comment has been submitted for review", Toast.LENGTH_SHORT).show()
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

    /*
    private fun generatePdf() {
        val pdfFileName = "Article_${articleId}.pdf"
        val pdfFilePath = File(getExternalFilesDir(null), pdfFileName)
        val writer = PdfWriter(pdfFilePath)
        val pdf = com.itextpdf.kernel.pdf.PdfDocument(writer)
        val document = Document(pdf)

        // Write article details to PDF
        val title = binding.tvTitle.text.toString()
        val author = binding.tvAuthor.text.toString()
        val date = binding.tvDate.text.toString()
        val description = binding.tvDescription.text.toString()
        val room = binding.tvRoom.text.toString()
        val time = binding.tvTime.text.toString()

        val content = "Title: $title\nAuthor: $author\nDate: $date\nDescription: $description\nRoom: $room\nTime: $time"

        // Add content to PDF document
        document.add(Paragraph(content))

        document.close()

        Toast.makeText(this, "PDF Generated: $pdfFileName", Toast.LENGTH_SHORT).show()

        // Open generated PDF file using Intent
        val pdfFileUri = FileProvider.getUriForFile(this, "com.example.confconnect.file", pdfFilePath)
        val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfFileUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(pdfIntent)
    } */
}
