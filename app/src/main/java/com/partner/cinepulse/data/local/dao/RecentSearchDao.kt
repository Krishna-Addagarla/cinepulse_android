package com.partner.cinepulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.partner.cinepulse.data.local.entity.RecentSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()

    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSearchesFlow(limit: Int = 15): Flow<List<RecentSearchEntity>>

    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC")
    suspend fun getRecentSearchesRaw(): List<RecentSearchEntity>

    @Transaction
    suspend fun addSearchWithLimit(query: String, limit: Int = 15) {
        val trimmed = query.trim()
        if (trimmed.isBlank() || trimmed.length < 2) return
        
        insert(RecentSearchEntity(query = trimmed, timestamp = System.currentTimeMillis()))
        val list = getRecentSearchesRaw()
        if (list.size > limit) {
            val toDelete = list.subList(limit, list.size)
            for (item in toDelete) {
                deleteByQuery(item.query)
            }
        }
    }
}
