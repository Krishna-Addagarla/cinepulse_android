package com.partner.cinepulse.data.local.entity

import androidx.room.Entity

@Entity(tableName = "recent_views", primaryKeys = ["contentId", "contentType"])
data class RecentViewEntity(
    val contentId: Int,
    val contentType: String, // "movie" | "tv_show"
    val timestamp: Long
)
