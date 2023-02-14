package me.nishant.data.user

interface UserDataSource {
    suspend fun getUserByEmail(email: String): User?
    suspend fun insertNewUser(user: User): Boolean
}