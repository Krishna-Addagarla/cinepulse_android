package com.partner.cinepulse.data.local
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.partner.cinepulse.data.local.dao.TokenDao
import com.partner.cinepulse.data.local.entity.TokenEntity

@Database(
    entities = [TokenEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CinePulseDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao

    companion object {
        @Volatile
        private var INSTANCE: CinePulseDatabase? = null

        fun getDatabase(context: Context): CinePulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CinePulseDatabase::class.java,
                    "cinepulse_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

