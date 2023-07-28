package com.melikeg.chatty.presentation.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.domain.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SigninViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private val _signInStatus = MutableLiveData<Resource<AuthResult>>()
    val signInStatus: LiveData<Resource<AuthResult>> get() = _signInStatus

    fun signInWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        firebaseRepository.signInWithEmailAndPassword(email, password).collectLatest {
            when(it){
                is Resource.Loading -> _signInStatus.value = Resource.Loading
                is Resource.Success -> _signInStatus.value = Resource.Success(it.result)
                is Resource.Error -> _signInStatus.value = Resource.Error(it.exception.toString())
            }
        }
    }

}