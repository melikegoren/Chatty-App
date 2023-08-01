package com.melikeg.chatty.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.domain.model.User
import com.melikeg.chatty.domain.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseRepository: FirebaseRepository): ViewModel() {

    private var _signOutStatus = MutableLiveData<Boolean>()
    val signOutStatus: LiveData<Boolean> = _signOutStatus

    private val _usernameLiveData = MutableLiveData<User?>() //??
    val usernameLiveData: LiveData<User?> get() = _usernameLiveData

    private var _userList = MutableLiveData<Resource<List<User>>>()
    val userList: LiveData<Resource<List<User>>> get() = _userList

    private var _favUserList = MutableLiveData<Resource<List<User>>>()
    val favUserList: LiveData<Resource<List<User>>> get() = _favUserList

    private var _userLiveData=  MutableLiveData<User?>()
    val userLiveData: LiveData<User?> get() = _userLiveData

    private var _userIsFav = MutableLiveData<Boolean>()
    val userIsFav: LiveData<Boolean> = _userIsFav


    fun signOut() = viewModelScope.launch{
        auth.signOut()
        _signOutStatus.value = true

    }

    fun fetchUser(email: String){
        viewModelScope.launch {
            val user = firebaseRepository.getUser(email)
            _usernameLiveData.value = user
        }
    }

    fun provideUser(email: String){
        viewModelScope.launch {
            val user = firebaseRepository.getUser(email)
            _userLiveData.value = user
        }

    }

    fun getUsers(currentUsername: String) = viewModelScope.launch {
        firebaseRepository.getAllUsers(currentUsername).collectLatest {
            when(it){
                is Resource.Loading -> _userList.value = Resource.Loading
                is Resource.Success -> _userList.value = Resource.Success(it.result)
                is Resource.Error -> _userList.value = Resource.Error(it.exception.toString())
            }

        }
    }

    fun saveFavUser(signedInUsername: String, favUser: User) = viewModelScope.launch {
        firebaseRepository.saveFavUsers(signedInUsername, favUser)
    }
    fun getFavUsers(currentUsername: String) = viewModelScope.launch {
        firebaseRepository.getFavUsers(currentUsername).collectLatest {
            when(it){
                is Resource.Loading -> _favUserList.value = Resource.Loading
                is Resource.Success -> _favUserList.value = Resource.Success(it.result)
                is Resource.Error -> _favUserList.value = Resource.Error(it.exception.toString())

            }
        }
    }

    fun checkIfFav(signedInUsername: String, contact: User){
        viewModelScope.launch {
            _userIsFav.value = firebaseRepository.checkIfFav(signedInUsername,contact)
        }
    }

    fun removeFromFav(signedInUser: User, userToRemove: User){
        viewModelScope.launch {
            firebaseRepository.removeUserFromFav(signedInUser,userToRemove)
        }

    }

}