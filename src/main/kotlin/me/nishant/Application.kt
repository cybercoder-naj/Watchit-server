package me.nishant

import io.ktor.server.application.*
import me.nishant.di.AppModule
import me.nishant.plugins.*
import me.nishant.security.token.TokenConfig

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    // @Inject
    val userDataSource = AppModule.userDataSource(
        AppModule.database(
            AppModule.connectionString(
                AppModule.dbName()
            ),
            AppModule.dbName()
        )
    )
    val tokenService = AppModule.tokenService()
    val hashingService = AppModule.hashingService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.issuer").getString(),
        expiresIn = 1000L * 60L * 60L * 24L * 365L,
        secret = System.getenv("JWT_SECRET"),
    )

    configureSecurity(tokenConfig)
    configureMonitoring()
    configureSerialization()
    configureRouting(userDataSource, tokenService, hashingService, tokenConfig)
}
