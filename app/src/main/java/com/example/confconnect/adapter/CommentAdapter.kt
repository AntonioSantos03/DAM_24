package com.example.confconnect.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.confconnect.Comment
import com.example.confconnect.databinding.ItemCommentBinding

class CommentAdapter(
    private var comments: List<Comment>,
    private val onItemClick: (Comment) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Optionally, set up click listener here if needed
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(comments[position])
                }
            }
        }

        fun bind(comment: Comment) {
            binding.apply {
                tvCommentText.text = comment.commentText
                tvUserName.text = comment.userName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    fun updateComments(updatedComments: List<Comment>) {
        comments = updatedComments
        notifyDataSetChanged()
    }
}
