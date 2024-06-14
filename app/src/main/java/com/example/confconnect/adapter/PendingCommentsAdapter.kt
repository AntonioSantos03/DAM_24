package com.example.confconnect.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.confconnect.Comments
import com.example.confconnect.databinding.ItemPendingCommentBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Locale

class PendingCommentsAdapter(
    private var comments: List<Comments>,
    private val onApproveClick: (Comments) -> Unit,
    private val onRejectClick: (Comments) -> Unit
) : RecyclerView.Adapter<PendingCommentsAdapter.CommentViewHolder>() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val articlesRef: DatabaseReference = database.getReference("Articles")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemPendingCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding, onApproveClick, onRejectClick, articlesRef)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun setComments(comments: List<Comments>) {
        this.comments = comments
        notifyDataSetChanged()
    }

    fun clearComments() {
        this.comments = emptyList()
        notifyDataSetChanged()
    }

    class CommentViewHolder(
        private val binding: ItemPendingCommentBinding,
        private val onApproveClick: (Comments) -> Unit,
        private val onRejectClick: (Comments) -> Unit,
        private val articlesRef: DatabaseReference
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comments) {
            binding.tvUserName.text = comment.userName
            binding.tvComment.text = comment.commentText
            binding.tvTimestamp.text = SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.getDefault())
                .format(java.util.Date(comment.timestamp ?: 0))

            // Fetch the article title from the database
            articlesRef.child(comment.articleId ?: "").child("title").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val title = snapshot.getValue(String::class.java)
                    binding.tvArticleTitle.text = title ?: "Unknown Title"
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.tvArticleTitle.text = "Error loading title"
                }
            })

            binding.btnApprove.setOnClickListener {
                onApproveClick(comment)
            }

            binding.btnReject.setOnClickListener {
                onRejectClick(comment)
            }
        }
    }
}
