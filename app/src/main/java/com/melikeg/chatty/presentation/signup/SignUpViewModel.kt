package com.melikeg.chatty.presentation.signup

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.melikeg.chatty.R
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.common.showCustomToast
import com.melikeg.chatty.domain.model.User
import com.melikeg.chatty.domain.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository)
    : ViewModel() {
    private val _signUpStatus = MutableLiveData<Resource<AuthResult>>()
    val signUpStatus: LiveData<Resource<AuthResult>> get() = _signUpStatus

    fun signUpWithEmailAndPassword(email: String, password: String, username: String,context: Context) =
        viewModelScope.launch {
            val user = User(username, email)
            val usernameRef = firebaseRepository.getUsersReferance().child(username)
            val dataSnapshot = usernameRef.get().await()

            if(!dataSnapshot.exists()){
                firebaseRepository.signUpWithEmailAndPassword(email, password).collectLatest {
                    when(it){
                        is Resource.Loading -> _signUpStatus.value = Resource.Loading
                        is Resource.Success -> {
                            _signUpStatus.value = Resource.Success(it.result)
                            usernameRef.setValue(user).await()
                        }
                        is Resource.Error -> _signUpStatus.value = Resource.Error(it.exception.toString())
                    }
                }
            }
            else{
                context.showCustomToast("Username is already taken.", R.drawable.baseline_warning_24 )
            }
        }
}