package me.nishant

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.nishant.data.requests.AuthRequest
import me.nishant.data.user.User
import me.nishant.data.user.UserDataSource
import me.nishant.security.hashing.HashingService

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post("signup") {
        val request = call.receiveNullable<AuthRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.email.isBlank()
                || request.password.isBlank()
                || request.firstName.isBlank()
                || request.lastName.isBlank()
        val isPwdShort = request.password.length < 8
        if (areFieldsBlank || isPwdShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataSource.insertNewUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}