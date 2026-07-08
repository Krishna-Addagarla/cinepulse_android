package com.partner.cinepulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.partner.cinepulse.data.local.entity.RecentViewEntity

@Dao
interface RecentViewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecentViewEntity)

    @Query("DELETE FROM recent_views WHERE contentId = :contentId AND contentType = :contentType")
    suspend fun deleteByKeys(contentId: Int, contentType: String)

    @Query("SELECT * FROM recent_views ORDER BY timestamp DESC")
    suspend fun getRecentViewsRaw(): List<RecentViewEntity>

    @Transaction
    suspend fun addViewWithLimit(contentId: Int, contentType: String, limit: Int = 20) {
        insert(RecentViewEntity(contentId = contentId, contentType = contentType, timestamp = System.currentTimeMillis()))
        val list = getRecentViewsRaw()
        if (list.size > limit) {
            val toDelete = list.subList(limit, list.size)
            for (item in toDelete) {
                deleteByKeys(item.contentId, item.contentType)
            }
        }
    }

    @Query("SELECT * FROM recent_views ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentViews(limit: Int = 20): List<RecentViewEntity>
}
