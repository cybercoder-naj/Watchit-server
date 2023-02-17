package me.nishant.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import me.nishant.authenticate
import me.nishant.data.user.UserDataSource
import me.nishant.getSecretInfo
import me.nishant.security.hashing.HashingService
import me.nishant.security.token.TokenConfig
import me.nishant.security.token.TokenService
import me.nishant.signIn
import me.nishant.signUp

fun Application.configureRouting(
    userDataSource: UserDataSource,
    tokenService: TokenService,
    hashingService: HashingService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(hashingService, tokenService, userDataSource, tokenConfig)
        signUp(hashingService, userDataSource)
        authenticate()
        getSecretInfo()
    }
}
