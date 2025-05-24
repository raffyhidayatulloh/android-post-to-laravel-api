package com.raffy.apitask

data class LoginResponse(
    val token: String,
    val user: User
)