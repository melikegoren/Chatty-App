package com.melikeg.chatty.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.domain.model.Message
import com.melikeg.chatty.domain.model.User
import com.melikeg.chatty.domain.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
): FirebaseRepository {

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://chatty-8cb2a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")

    private val databaseReferenceMessages: DatabaseReference =
        FirebaseDatabase.getInstance("https://chatty-8cb2a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("messages")

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading)
        emit(Resource.Success(firebaseAuth.createUserWithEmailAndPassword(email, password).await()))
    }.catch {
        emit(Resource.Error(it.message))
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading)
        emit(Resource.Success(firebaseAuth.signInWithEmailAndPassword(email, password).await()))
    }.catch {
        emit(Resource.Error(it.message))
    }

    override suspend fun getUser(email: String): User? {
        return withContext(Dispatchers.IO) {
            var user: User? = null

            val query = databaseReference.orderByChild("email").equalTo(email)
            val snapshot = query.get().await()

            if (snapshot.exists()) {
                val userSnapshot = snapshot.children.first()
                user = userSnapshot.getValue(User::class.java)
            }

            user
        }
    }

    override suspend fun saveUserWithUsername(user: User) {
        return withContext(Dispatchers.IO) {
            val username = user.username
            val usernameRef = databaseReference.child(username)

            val dataSnapshot = usernameRef.get().await()
            if (dataSnapshot.exists()) {
            } else {
                usernameRef.setValue(user).await()

            }
        }
    }

    override suspend fun getAllUsers(currentUsername: String): Flow<Resource<MutableList<User>>> = flow{
        emit(Resource.Loading)
        val userList = mutableListOf<User>()
        val snapshot = databaseReference.get().await()
        for (userSnapshot in snapshot.children) {
            val user = userSnapshot.getValue(User::class.java)
            if(currentUsername == user?.username) continue
            user?.let {
                userList.add(it)
            }

        }
        emit(Resource.Success(userList))

    }.catch {
        emit(Resource.Error(it.message))
    }

    override fun getMessagesReference(): DatabaseReference {
        return FirebaseDatabase.getInstance("https://chatty-8cb2a-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("messages")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun sendMessage(senderId: String, receiverId: String, messageText: String){
        withContext(Dispatchers.IO) {
            val messageId = databaseReferenceMessages.push().key ?: ""
            val message = Message(messageId, senderId, receiverId, messageText)
            databaseReferenceMessages.child(messageId).setValue(message).await()
        }
    }

}