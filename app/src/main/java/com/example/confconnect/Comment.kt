package com.example.confconnect

import com.google.firebase.Timestamp

data class Comment(
    val commentId: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val commentText: String? = null,
    val timestamp: Timestamp? = null,
)
