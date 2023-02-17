package me.nishant.di

import me.nishant.data.user.MongoUserDataSource
import me.nishant.data.user.UserDataSource
import me.nishant.security.hashing.HashingService
import me.nishant.security.hashing.SHA256HashingService
import me.nishant.security.token.JwtTokenService
import me.nishant.security.token.TokenService
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object AppModule {

    fun dbName(): String {
        return "watchit"
    }

    fun connectionString(dbName: String): String {
        val password = System.getenv("MONGO_PWD")
        return "mongodb+srv://nishant:$password@watchit.fqccnat.mongodb.net/$dbName?retryWrites=true&w=majority"
    }

    fun database(connectionString: String, dbName: String): CoroutineDatabase {
        return KMongo
            .createClient(connectionString)
            .coroutine
            .getDatabase(dbName)
    }

    fun userDataSource(db: CoroutineDatabase): UserDataSource {
        return MongoUserDataSource(db)
    }

    fun tokenService(): TokenService {
        return JwtTokenService()
    }

    fun hashingService(): HashingService {
        return SHA256HashingService()
    }
}