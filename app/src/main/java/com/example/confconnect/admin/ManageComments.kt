package com.example.confconnect.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.Comments
import com.example.confconnect.adapter.PendingCommentsAdapter
import com.example.confconnect.databinding.ActivityManageCommentsBinding
import com.google.firebase.database.*

class ManageComments : AppCompatActivity() {

    private lateinit var binding: ActivityManageCommentsBinding
    private lateinit var pendingCommentsAdapter: PendingCommentsAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var commentsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        commentsRef = database.getReference("comments")

        binding.rvPendingComments.layoutManager = LinearLayoutManager(this)
        pendingCommentsAdapter = PendingCommentsAdapter(emptyList(), ::onApproveComment)
        binding.rvPendingComments.adapter = pendingCommentsAdapter

        loadPendingComments()
    }

    private fun loadPendingComments() {
        commentsRef.orderByChild("approved").equalTo(false)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pendingComments = mutableListOf<Comments>()
                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comments::class.java)
                        comment?.let { pendingComments.add(it) }
                    }
                    pendingCommentsAdapter.submitList(pendingComments)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ManageComments, "Failed to load pending comments: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun onApproveComment(comment: Comments) {
        // Update comment approval status to true
        commentsRef.child(comment.commentId ?: "").child("approved").setValue(true)
            .addOnSuccessListener {
                Toast.makeText(this, "Comment approved successfully", Toast.LENGTH_SHORT).show()
                loadPendingComments() // Refresh the list after approval
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to approve comment: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
