package com.partner.cinepulse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_cache")
data class SearchCacheEntity(
    @PrimaryKey
    val key: String, // "trending", "suggestions", or "last_search_results"
    val responseJson: String,
    val timestamp: Long
)
