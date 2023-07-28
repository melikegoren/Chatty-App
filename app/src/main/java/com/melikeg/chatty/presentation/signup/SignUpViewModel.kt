package com.melikeg.chatty.presentation.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.domain.model.User
import com.melikeg.chatty.domain.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository)
    : ViewModel() {
    private val _signUpStatus = MutableLiveData<Resource<AuthResult>>()
    val signUpStatus: LiveData<Resource<AuthResult>> get() = _signUpStatus



    fun signUpWithEmailAndPassword(email: String, password: String, username: String) = viewModelScope.launch {
        val user = User(username, email)
        firebaseRepository.saveUserWithUsername(user)
        firebaseRepository.signUpWithEmailAndPassword(email, password).collectLatest {
            when(it){
                is Resource.Loading -> _signUpStatus.value = Resource.Loading
                is Resource.Success -> {
                    _signUpStatus.value = Resource.Success(it.result)

                }
                is Resource.Error -> _signUpStatus.value = Resource.Error(it.exception.toString())
            }
        }
    }
}