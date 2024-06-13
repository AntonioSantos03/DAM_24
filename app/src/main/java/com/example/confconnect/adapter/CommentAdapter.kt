package com.example.confconnect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.confconnect.Comments
import com.example.confconnect.R
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(
    private var comments: List<Comments>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun setComments(comments: List<Comments>) {
        this.comments = comments
        notifyDataSetChanged()
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.tvUserName)
        private val commentTextView: TextView = itemView.findViewById(R.id.tvCommentText)
        private val timestampTextView: TextView = itemView.findViewById(R.id.tvTimestamp)

        fun bind(comment: Comments) {
            userNameTextView.text = comment.userName
            commentTextView.text = comment.commentText
            val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.getDefault())
            val formattedDate = sdf.format(Date(comment.timestamp ?: 0))
            timestampTextView.text = formattedDate
        }
    }
}
