package me.nishant.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class AuthSignUpRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)