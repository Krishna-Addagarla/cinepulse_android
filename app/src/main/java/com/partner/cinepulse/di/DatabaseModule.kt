package com.partner.cinepulse.di

import android.content.Context
import androidx.room.Room
import com.partner.cinepulse.data.local.CinePulseDatabase
import com.partner.cinepulse.data.local.MIGRATION_1_2
import com.partner.cinepulse.data.local.dao.TokenDao
import com.partner.cinepulse.data.local.dao.RecentSearchDao
import com.partner.cinepulse.data.local.dao.RecentViewDao
import com.partner.cinepulse.data.local.dao.SearchCacheDao
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
        ).addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideTokenDao(database: CinePulseDatabase): TokenDao {
        return database.tokenDao()
    }

    @Provides
    @Singleton
    fun provideRecentSearchDao(database: CinePulseDatabase): RecentSearchDao {
        return database.recentSearchDao()
    }

    @Provides
    @Singleton
    fun provideRecentViewDao(database: CinePulseDatabase): RecentViewDao {
        return database.recentViewDao()
    }

    @Provides
    @Singleton
    fun provideSearchCacheDao(database: CinePulseDatabase): SearchCacheDao {
        return database.searchCacheDao()
    }
}