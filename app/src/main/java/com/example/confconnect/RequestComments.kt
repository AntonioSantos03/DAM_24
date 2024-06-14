package com.example.confconnect

data class RequestComments(
    val commentId: String? = "",
    val articleId: String? = "",
    val userId: String? = "",
    val userName: String? = "",
    val commentText: String? = "",
    val timestamp: Long? = null,
)
