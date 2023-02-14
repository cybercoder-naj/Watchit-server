package me.nishant

import io.ktor.server.application.*
import me.nishant.di.AppModule
import me.nishant.plugins.*

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
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureRouting()
}
