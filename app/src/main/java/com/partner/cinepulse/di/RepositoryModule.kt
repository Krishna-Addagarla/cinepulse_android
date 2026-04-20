package com.partner.cinepulse.di

import com.partner.cinepulse.data.repository.AuthRepository
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.data.repository.FanClubRepository
import com.partner.cinepulse.data.repositoryImpl.AuthRepositoryImpl
import com.partner.cinepulse.data.repositoryImpl.ContentRepositoryImpl
import com.partner.cinepulse.data.repositoryImpl.FanClubRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindContentRepository(
        contentRepositoryImpl : ContentRepositoryImpl
    ) : ContentRepository

    @Binds
    @Singleton
    abstract fun bindFanClubRepository(
        fanClubRepositoryImpl: FanClubRepositoryImpl
    ) : FanClubRepository
}