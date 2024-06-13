package com.example.confconnect

data class Comments(
    val commentId: String? = "",
    val articleId: String? = "",
    val userId: String? = "",
    val userName: String? = "",
    val commentText: String? = "",
    val timestamp: Long? = null,
)
