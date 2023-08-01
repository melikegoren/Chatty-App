package com.melikeg.chatty.di

import com.melikeg.chatty.data.repository.FirebaseRepositoryImpl
import com.melikeg.chatty.domain.repository.FirebaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {
    @Provides
    fun provideRepository(
        firebaseRepository: FirebaseRepositoryImpl
    ): FirebaseRepository = firebaseRepository
}