package me.nishant.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class AuthSignInRequest(
    val email: String,
    val password: String
)