package com.melikeg.chatty.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.domain.model.Message
import com.melikeg.chatty.domain.model.User
import com.melikeg.chatty.domain.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
): FirebaseRepository {

    private val databaseReferenceUsers: DatabaseReference =
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

            val query = databaseReferenceUsers.orderByChild("email").equalTo(email)
            val snapshot = query.get().await()

            if (snapshot.exists()) {
                val userSnapshot = snapshot.children.first()
                user = userSnapshot.getValue(User::class.java)
            }

            user
        }
    }

    override suspend fun getAllUsers(currentUsername: String): Flow<Resource<MutableList<User>>> = flow{
        emit(Resource.Loading)
        val userList = mutableListOf<User>()
        val snapshot = databaseReferenceUsers.get().await()
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

    override suspend fun getFavUsers(currentUsername: String): Flow<Resource<MutableList<User>>> = callbackFlow <Resource<MutableList<User>>> {
        trySend(Resource.Loading)
        val userList = mutableListOf<User>()
        val userRef = getFavUsersReference().child(currentUsername)
        val valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(userList.isNotEmpty()) userList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        userList.add(it)
                    }
                }
                trySend(Resource.Success(userList))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }

        }
        userRef.addValueEventListener(valueEventListener)

        awaitClose{
            userRef.removeEventListener(valueEventListener)
        }

    }.catch {
        emit(Resource.Error(it.message.toString()))
    }

    override suspend fun saveFavUsers(signedInUsername: String, favUser: User) {
        withContext(Dispatchers.IO){
            val snapshot = getFavUsersReference()
            val userRef = snapshot.child(signedInUsername).child(favUser.username)
            userRef.setValue(favUser)
        }
    }

    override suspend fun checkIfFav(signedInUsername: String, contact: User): Boolean {
        return withContext(Dispatchers.IO){
            var check = false
            val snapshot = getFavUsersReference().child(signedInUsername).get().await()
            for (userSnapshot in snapshot.children) {
                val user = userSnapshot.getValue(User::class.java)
                if(user?.username == contact.username){
                    check = true
                }
            }
            check
        }
    }

    override suspend fun removeUserFromFav(signedInUser: User, userToRemove: User) {
        withContext(Dispatchers.IO){
            val a = getFavUsersReference().child(signedInUser.username).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(userSnapshot in snapshot.children ){
                        val user = userSnapshot.getValue(User::class.java)
                        if(user?.username == userToRemove.username){
                            userSnapshot.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })

        }

    }

    override fun getMessagesReference(): DatabaseReference {
        return firebaseDatabase.reference.child("messages")
    }

    override fun getUsersReferance(): DatabaseReference {
        return firebaseDatabase.reference.child("users")
    }

    override fun getFavUsersReference(): DatabaseReference {
        return firebaseDatabase.reference.child("favUsers")
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