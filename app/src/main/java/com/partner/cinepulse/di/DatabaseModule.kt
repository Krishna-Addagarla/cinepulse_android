package com.partner.cinepulse.di

import android.content.Context
import androidx.room.Room
import com.partner.cinepulse.data.local.CinePulseDatabase
import com.partner.cinepulse.data.local.dao.TokenDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): CinePulseDatabase {
        return Room.databaseBuilder(
            context,
            CinePulseDatabase::class.java,
            "cinepulse_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTokenDao(database: CinePulseDatabase): TokenDao {
        return database.tokenDao()
    }
}