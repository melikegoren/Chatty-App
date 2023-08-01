package com.melikeg.chatty.presentation.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.domain.model.Message
import com.melikeg.chatty.domain.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private val messagesReference: DatabaseReference
        get() = firebaseRepository.getMessagesReference()

    private val _sendMessageResult = MutableLiveData<Resource<Unit>>()
    val sendMessageResult: LiveData<Resource<Unit>> = _sendMessageResult

    private val _messagesList = MutableLiveData<Resource<List<Message>>>()
    val messagesList: LiveData<Resource<List<Message>>> = _messagesList

    fun sendMessage(senderId: String, receiverId: String, messageContent: String) =
        viewModelScope.launch {

            try {
                firebaseRepository.sendMessage(senderId, receiverId, messageContent)
                _sendMessageResult.postValue(Resource.Success(Unit))
            } catch (e: Exception) {
                _sendMessageResult.postValue(Resource.Error(e.message.toString()))
            }

        }

    fun listenForMessages(senderId: String, receiverId: String) {
        messagesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messagesList = mutableListOf<Message>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    if (message != null &&
                        ((message.senderId == senderId && message.receiverId == receiverId) ||
                                (message.senderId == receiverId && message.receiverId == senderId))
                    ) {
                        messagesList.add(message)
                    }
                }
                _messagesList.value = Resource.Success(messagesList)
            }

            override fun onCancelled(error: DatabaseError) {
                _messagesList.value = Resource.Error("Error fetching messages.")
            }
        })
    }
}