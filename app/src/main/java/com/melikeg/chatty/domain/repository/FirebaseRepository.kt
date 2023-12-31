package com.melikeg.chatty.domain.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DatabaseReference
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.domain.model.User
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<AuthResult>>

    suspend fun getUser(email: String): User?

    //suspend fun saveUserWithUsername(user: User)

    suspend fun getAllUsers(currentUsername: String): Flow<Resource<MutableList<User>>>

    suspend fun getFavUsers(currentUsername: String): Flow<Resource<MutableList<User>>>

    suspend fun saveFavUsers(signedInUsername: String,favUser: User)

    suspend fun checkIfFav(signedInUsername: String, contact: User): Boolean

    suspend fun removeUserFromFav(signedInUser: User, userToRemove: User)

    suspend fun sendMessage( senderId: String, receiverId: String, messageText: String)

    fun getMessagesReference(): DatabaseReference

    fun getUsersReferance(): DatabaseReference

    fun getFavUsersReference(): DatabaseReference

}