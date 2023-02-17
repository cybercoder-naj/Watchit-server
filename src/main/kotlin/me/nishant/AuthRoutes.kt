package me.nishant

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.nishant.authenticate
import me.nishant.data.requests.AuthSignInRequest
import me.nishant.data.requests.AuthSignUpRequest
import me.nishant.data.responses.AuthResponse
import me.nishant.data.user.User
import me.nishant.data.user.UserDataSource
import me.nishant.security.hashing.HashingService
import me.nishant.security.hashing.SaltedHash
import me.nishant.security.token.TokenClaim
import me.nishant.security.token.TokenConfig
import me.nishant.security.token.TokenService

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post("signup") {
        val request = call.receiveNullable<AuthSignUpRequest>() ?: run {
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

fun Route.signIn(
    hashingService: HashingService,
    tokenService: TokenService,
    userDataSource: UserDataSource,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receiveNullable<AuthSignInRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Check your request body")
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound, "No user with ${request.email} found")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}