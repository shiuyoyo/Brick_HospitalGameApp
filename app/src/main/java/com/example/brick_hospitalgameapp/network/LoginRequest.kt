package com.example.brick_hospitalgameapp.network

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val user_id: String,
    val password: String
)
