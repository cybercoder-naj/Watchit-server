package me.nishant.di

import me.nishant.data.user.MongoUserDataSource
import me.nishant.data.user.UserDataSource
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
}