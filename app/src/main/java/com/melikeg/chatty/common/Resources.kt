package com.melikeg.chatty.common

sealed class Resource<out T : Any> {
    object Loading : Resource<Nothing>()
    data class Success<out T : Any>(val result: T?) : Resource<T>()
    data class Error(val exception: String?) : Resource<Nothing>()
}
