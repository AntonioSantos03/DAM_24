package com.example.confconnect

data class Articles(
    val id: String? = null,
    val title: String? = null,
    val author: String? = null,
    val date: String? = null,
    val room: String? = null,
    val description: String? = null,
    val comments: List<String>? = null
)
