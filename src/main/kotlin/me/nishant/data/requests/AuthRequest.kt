package me.nishant.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)