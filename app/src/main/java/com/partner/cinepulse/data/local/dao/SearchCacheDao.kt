package com.partner.cinepulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.partner.cinepulse.data.local.entity.SearchCacheEntity

@Dao
interface SearchCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCache(cache: SearchCacheEntity)

    @Query("SELECT * FROM search_cache WHERE `key` = :key")
    suspend fun getCache(key: String): SearchCacheEntity?

    @Query("DELETE FROM search_cache WHERE `key` = :key")
    suspend fun deleteCache(key: String)

    @Query("DELETE FROM search_cache WHERE :now - timestamp > :expiryThresholdMs")
    suspend fun cleanupExpiredCache(now: Long, expiryThresholdMs: Long = 24 * 60 * 60 * 1000L)
}
