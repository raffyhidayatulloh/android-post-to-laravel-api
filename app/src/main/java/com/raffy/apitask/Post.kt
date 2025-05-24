package com.raffy.apitask

data class Post(
    val id: Int,
    val user_id: Int,
    val title: String,
    val body: String,
    val created_at: String,
    val updated_at: String,
    val user: User? = null
)
