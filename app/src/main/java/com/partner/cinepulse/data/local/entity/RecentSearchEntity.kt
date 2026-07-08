package com.partner.cinepulse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey
    val query: String, // Case-insensitivity NOCASE is configured via Migration
    val timestamp: Long
)
