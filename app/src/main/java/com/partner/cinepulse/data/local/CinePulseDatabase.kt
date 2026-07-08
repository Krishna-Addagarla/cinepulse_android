package com.partner.cinepulse.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.partner.cinepulse.data.local.dao.TokenDao
import com.partner.cinepulse.data.local.dao.RecentSearchDao
import com.partner.cinepulse.data.local.dao.RecentViewDao
import com.partner.cinepulse.data.local.dao.SearchCacheDao
import com.partner.cinepulse.data.local.entity.TokenEntity
import com.partner.cinepulse.data.local.entity.RecentSearchEntity
import com.partner.cinepulse.data.local.entity.RecentViewEntity
import com.partner.cinepulse.data.local.entity.SearchCacheEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `recent_searches` (`query` TEXT COLLATE NOCASE NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`query`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `recent_views` (`contentId` INTEGER NOT NULL, `contentType` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`contentId`, `contentType`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `search_cache` (`key` TEXT NOT NULL, `responseJson` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`key`))"
        )
    }
}

@Database(
    entities = [
        TokenEntity::class,
        RecentSearchEntity::class,
        RecentViewEntity::class,
        SearchCacheEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CinePulseDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun recentViewDao(): RecentViewDao
    abstract fun searchCacheDao(): SearchCacheDao

    companion object {
        @Volatile
        private var INSTANCE: CinePulseDatabase? = null

        fun getDatabase(context: Context): CinePulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CinePulseDatabase::class.java,
                    "cinepulse_database"
                ).addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
